package com.example.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.web.entity.AppointRecord;
import com.example.web.entity.Integral;
import com.example.web.entity.Room;
import com.example.web.entity.Seat;
import com.example.web.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AiContextServiceImpl implements AiContextService {

    @Autowired
    private RoomService roomService;

    @Autowired
    private SeatService seatService;

    @Autowired
    private AppointRecordService appointRecordService;

    @Autowired
    private IntegralService integralService;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public String buildContext(String question, Integer userId) {
        StringBuilder ctx = new StringBuilder();
        ctx.append("【系统概览】\n");
        ctx.append("这是一个基于 Spring Boot + Vue.js 开发的【志高自习室预约管理系统】。\n");
        ctx.append("核心功能：在线预约自习室座位、积分奖励、取消规则、实时座位图。\n\n");

        ctx.append("【业务规则】\n");
        ctx.append("1. 预约流程：选择自习室 → 选择日期 → 点击空闲座位 → 确认预约。\n");
        ctx.append("2. 预约类型：按时间段预约（上午/下午/晚上或自定义时段）。\n");
        ctx.append("3. 取消规则：可在预约开始前取消，每月有最大可取消次数上限（各自习室不同）。\n");
        ctx.append("4. 积分规则：成功预约并完成使用可获得积分奖励；按时签到可获得积分。积分记录显示积分值与来源。\n");
        ctx.append("5. 座位状态：绿色=空闲可预约；红色=已被预约；灰色=维修中不可用。\n");
        ctx.append("6. 账号规则：用户需先注册登录才能预约；忘记密码可通过找回密码功能重置。\n\n");

        // 根据问题关键词，检索相关数据
        String q = question != null ? question.toLowerCase() : "";

        // ==================== 自习室相关 ====================
        if (q.contains("自习室") || q.contains("哪些") || q.contains("介绍") ||
            q.contains("room") || q.contains("有什么") || q.contains("列表") ||
            q.contains("推荐") || q.contains("怎么") || q.contains("预约") ||
            q.contains("座位") || q.contains("地方") || q.contains("地址")) {

            List<Room> rooms = roomService.list();
            if (rooms != null && !rooms.isEmpty()) {
                ctx.append("【系统中的自习室列表】\n");
                for (int i = 0; i < Math.min(rooms.size(), 10); i++) {
                    Room r = rooms.get(i);
                    ctx.append((i + 1) + "). " + (r.getName() != null ? r.getName() : "未知自习室"));
                    if (r.getAddress() != null) ctx.append(" | 位置：" + r.getAddress());
                    if (r.getEveryMonCancelCount() != null) ctx.append(" | 月可取消次数：" + r.getEveryMonCancelCount());
                    if (r.getContent() != null && !r.getContent().trim().isEmpty()) {
                        String content = r.getContent().replaceAll("\\<.*?\\>", "").trim();
                        if (content.length() > 100) content = content.substring(0, 100) + "...";
                        ctx.append(" | 介绍：" + content);
                    }
                    ctx.append("\n");

                    // 该自习室的座位数
                    try {
                        long seatCount = seatService.count(
                            new QueryWrapper<Seat>().eq("RoomId", r.getId()));
                        long availableCount = seatService.count(
                            new QueryWrapper<Seat>().eq("RoomId", r.getId())
                                .and(w -> w.isNull("IsMaintain").or().eq("IsMaintain", false)));
                        long maintainCount = seatService.count(
                            new QueryWrapper<Seat>().eq("RoomId", r.getId()).eq("IsMaintain", true));
                        ctx.append("   座位总数：" + seatCount + "，可用：" + availableCount + "，维修中：" + maintainCount + "\n");
                    } catch (Exception e) { /* 忽略 */ }
                }
                ctx.append("共 " + rooms.size() + " 个自习室。\n\n");
            } else {
                ctx.append("【系统中暂无自习室数据】\n\n");
            }
        }

        // ==================== 座位状态查询 ====================
        if (q.contains("座位") || q.contains("空闲") || q.contains("还有") ||
            q.contains("预约") || q.contains("位置")) {

            try {
                long totalSeats = seatService.count();
                long availableSeats = seatService.count(
                    new QueryWrapper<Seat>().and(w -> w.isNull("IsMaintain").or().eq("IsMaintain", false)));
                long maintainSeats = seatService.count(
                    new QueryWrapper<Seat>().eq("IsMaintain", true));

                ctx.append("【座位整体状态】\n");
                ctx.append("总座位数：" + totalSeats + "，可用：" + availableSeats + "，维修中：" + maintainSeats + "\n\n");
            } catch (Exception e) { /* 忽略 */ }
        }

        // ==================== 积分相关 ====================
        if (q.contains("积分") || q.contains("奖励") || q.contains("分")) {

            ctx.append("【积分规则说明】\n");
            ctx.append("- 用户通过完成预约、按时使用等行为可获得积分奖励。\n");
            ctx.append("- 每笔积分记录包含积分值（正数）和来源字段（描述积分获取方式）。\n");
            ctx.append("- 积分用于记录用户活跃度与贡献。\n\n");

            // 用户个人积分
            if (userId != null) {
                try {
                    List<Integral> userIntegrals = integralService.list(
                        new QueryWrapper<Integral>().eq("UserId", userId).orderByDesc("CreateTime"));
                    int totalIntegral = 0;
                    if (userIntegrals != null && !userIntegrals.isEmpty()) {
                        for (Integral ig : userIntegrals) {
                            if (ig.getIntegralValue() != null) totalIntegral += ig.getIntegralValue();
                        }
                        ctx.append("【用户当前积分】\n");
                        ctx.append("累计积分：" + totalIntegral + " 分（最近记录：");
                        if (!userIntegrals.isEmpty()) {
                            Integral ig = userIntegrals.get(0);
                            ctx.append("+" + ig.getIntegralValue() + "分，来源：" + (ig.getTitle() != null ? ig.getTitle() : ig.getSource()) + ")");
                        } else {
                            ctx.append("暂无记录)");
                        }
                        ctx.append("\n\n");
                    }
                } catch (Exception e) { /* 忽略 */ }
            } else {
                ctx.append("【提示】登录后可以查询您的个人积分记录。\n\n");
            }
        }

        // ==================== 用户个人预约 ====================
        if (userId != null && (q.contains("我的") || q.contains("预约记录") ||
            q.contains("已经") || q.contains("之前") || q.contains("下次"))) {

            try {
                List<AppointRecord> records = appointRecordService.list(
                    new QueryWrapper<AppointRecord>().eq("UserId", userId).orderByDesc("AppointDate"));

                ctx.append("【用户预约记录】\n");
                if (records == null || records.isEmpty()) {
                    ctx.append("暂无预约记录。\n\n");
                } else {
                    // 统计
                    long totalCount = records.size();
                    long futureCount = records.stream().filter(r -> {
                        if (r.getBeginTime() == null) return false;
                        return r.getBeginTime().isAfter(LocalDateTime.now());
                    }).count();
                    long pastCount = totalCount - futureCount;

                    ctx.append("总预约次数：" + totalCount + "，即将进行：" + futureCount + "，已完成/过期：" + pastCount + "\n");

                    // 显示最近 3 条
                    int showCount = Math.min(records.size(), 3);
                    for (int i = 0; i < showCount; i++) {
                        AppointRecord r = records.get(i);
                        String statusDesc = switch (r.getAppointStatus() == null ? 0 : r.getAppointStatus()) {
                            case 0 -> "待使用";
                            case 1 -> "已完成";
                            case 2 -> "已取消";
                            case 3 -> "已过期";
                            default -> "状态" + r.getAppointStatus();
                        };
                        String timeStr = r.getAppointDate() != null ? r.getAppointDate().format(DATE_FMT) : "未知时间";
                        String name = r.getName() != null ? r.getName() : "预约" + (i + 1);
                        ctx.append("  " + (i + 1) + "). " + name + " | " + timeStr + " | " + statusDesc);
                        if (r.getCommentScore() != null) ctx.append(" | 评分：" + r.getCommentScore());
                        ctx.append("\n");
                    }
                    ctx.append("\n");
                }
            } catch (Exception e) { /* 忽略 */ }
        }

        // ==================== 当前时间/日期 ====================
        ctx.append("【当前时间】\n");
        ctx.append(LocalDateTime.now().format(DATE_FMT) + "（系统时间）\n\n");

        // ==================== 操作提示 ====================
        ctx.append("【如何回答】\n");
        ctx.append("- 回答要基于【系统中的真实数据】，不要编造不存在的自习室、座位或规则。\n");
        ctx.append("- 如果【系统概览/业务规则】没有覆盖用户问题，可以基于上面的数据给出具体指引。\n");
        ctx.append("- 推荐操作以编号或步骤方式列出，简洁清晰。\n");
        ctx.append("- 遇到需要登录/身份验证的内容，要提示用户先登录。\n");

        return ctx.toString();
    }
}
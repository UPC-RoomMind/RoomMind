import { Message, MessageBox, Loading } from "element-ui"; //消息提示框
import store from "@/store";
import router from '@/router'

/**
 * 公共确认提示框
 * @param {*} config
 * @returns
 */
export function ConfirmMessageBox(config) {
    let _config = {
        title: config.title || "提示",
        content: config.content || "",
    };
    return new Promise(function (resolve, reject) {
        MessageBox.confirm(_config.content, _config.title, {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
        })
            .then(() => {
                resolve(true);
            })
            .catch(() => {
                resolve(false);
            });
    });
}


/**
 * 获取路径中的文件名称
 */
export function GetFileNameByPath(path) {
    if (!path) {
        return "";
    }
    var pos1 = path.lastIndexOf('/');
    var pos2 = path.lastIndexOf('\\');
    var pos = Math.max(pos1, pos2);
    if (pos < 0) {
        return path;
    }
    else {
        return path.substring(pos + 1);
    }
}
/**
 * 获取路径中的文件格式
 */
export function GetFileTypeByPath(path) {
    if (!path) {
        return "";
    }
    var pos = path.lastIndexOf('.');
    if (pos < 0) {
        return "";
    }
    else {
        return path.substring(pos + 1);
    }
}
/**
 * 根据路径获取文件的详细信息
 */
export function FullConvertUrlArray(value = '', cutting = ',') {
    var arr = ConvertArray(value, cutting);
    arr = arr.map(x => { return { url: ReplaceImageHttp(x), name: GetFileNameByPath(x), type: GetFileTypeByPath(x) }; });
    return arr;

}
/**
 * 处理图片路径
 * @param {*} value
 * @returns
 */
export function ReplaceImageHttp(value) {

    if (value == null || value == "") {
        return "";
    }
    //判断是不是数组 参数归一化
    if (Array.isArray(value)) {
        let temp = [];
        value.forEach((item) => {
            temp.push(ReplaceImageHttp(item))
        })

        return temp;
    }
    return value;
}

/**
 * 得到完整的时间格式 年-月-日 时分秒
 */
export function YMDHMSFormat(val) {
    if (!val) return "";
    let date = new Date(val);
    if (!date) return "";

    var y = date.getFullYear();
    var m = (date.getMonth() + 1).toString();
    m = parseInt(m) < 10 ? "0" + m : m;
    var d = date.getDate().toString();
    d = parseInt(d) < 10 ? "0" + d : d;
    var h = date.getHours().toString();
    h = parseInt(h) < 10 ? "0" + h : h;
    var minute = date.getMinutes().toString();
    minute = parseInt(minute) < 10 ? "0" + minute : minute;
    var second = date.getSeconds().toString();
    second = parseInt(second) < 10 ? "0" + second : second;
    return y + "-" + m + "-" + d + " " + h + ":" + minute + ":" + second;
}
/**
 * 得到完整的时间格式 年-月-日
 */
export function YMDFormat(val) {
    if (!val) return "";
    let date = new Date(val);
    if (!date) return "";

    var y = date.getFullYear();
    var m = (date.getMonth() + 1).toString();
    m = parseInt(m) < 10 ? "0" + m : m;
    var d = date.getDate().toString();
    d = parseInt(d) < 10 ? "0" + d : d;
    var h = date.getHours().toString();
    h = parseInt(h) < 10 ? "0" + h : h;
    var minute = date.getMinutes().toString();
    minute = parseInt(minute) < 10 ? "0" + minute : minute;
    var second = date.getSeconds().toString();
    second = parseInt(second) < 10 ? "0" + second : second;
    return y + "-" + m + "-" + d + " ";
}

export default {
    ConfirmMessageBox,
    FullConvertUrlArray,
    GetFileNameByPath,
    GetFileTypeByPath,
    ReplaceImageHttp,
    YMDHMSFormat,
    YMDFormat,
};

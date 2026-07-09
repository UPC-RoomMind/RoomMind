<template>
    <div class="upload-area">
    <el-upload
        ref="upload"
        class="upload-btn"
        :action="uploadUrl"
        :headers="uploadHeaders"
        list-type="picture-card"
        :show-file-list="false"
        :on-success="handleUploadSuccess"
        :on-error="handleUploadError"
        :before-upload="beforeUpload"
        :accept="acceptTypes"
        :multiple="multiple"
        :limit="limit">
        <div class="upload-trigger">
            <i class="el-icon-plus"></i>
            <span class="upload-tip">上传图片</span>
        </div>
    </el-upload>

    <div class="upload-info">
        <span class="tip-text">
            <i class="el-icon-info"></i>
            支持 {{ acceptTypes }} 格式，单张不超过 {{ maxSize }}MB，最多上传 {{ limit }} 张
        </span>
    </div>
    <div style="width:100%; height:0; flex-basis:100%;"></div>
<!-- ✅ 添加换行元素，强制图片列表到下一行 -->
    <div class="image-list-wrapper">
        <div v-if="fileList.length > 0" class="image-list">
            <div
                v-for="(item, index) in fileList"
                :key="index"
                class="image-item">
                <div class="image-card">
                    <img :src="item.url" :alt="item.name || '图片'" />
                    <div class="image-actions">
                        <el-button
                            v-if="showCrop"
                            type="primary"
                            size="mini"
                            circle
                            @click="openCropDialog(item, index)">
                            <i class="el-icon-crop"></i>
                        </el-button>
                        <el-button
                            type="info"
                            size="mini"
                            circle
                            @click="handlePreview(item)">
                            <i class="el-icon-zoom-in"></i>
                        </el-button>
                        <el-button
                            type="danger"
                            size="mini"
                            circle
                            @click="handleRemove(item, index)">
                            <i class="el-icon-delete"></i>
                        </el-button>
                    </div>
                    <span v-if="fileList.length > 1" class="image-index">{{ index + 1 }}</span>
                </div>
                <div class="image-name">{{ item.name || '未命名' }}</div>
            </div>
        </div>
    </div>

        <!-- ==================== 图片预览对话框 ==================== -->
<!-- 图片预览 -->
<el-dialog
    :visible.sync="previewVisible"
    append-to-body
    width="60%"
    class="preview-dialog"
    @close="previewUrl = ''">
    <img width="100%" :src="previewUrl" alt="预览图片" />
</el-dialog>

        <!-- ==================== 圈选裁剪对话框 ==================== -->
        <el-dialog
            :visible.sync="cropDialogVisible"
            append-to-body
            width="80%"
            class="crop-dialog"
            title="圈选展示区域"
            :close-on-click-modal="false"
            @close="closeCropDialog">
            <div class="crop-container">
                <!-- 裁剪组件 -->
                <div class="crop-wrapper">
                    <vue-cropper
                        ref="cropper"
                        :img="cropImageUrl"
                        :outputSize="1"
                        :outputType="'png'"
                        :info="true"
                        :canScale="true"
                        :autoCrop="true"
                        :autoCropWidth="cropWidth"
                        :autoCropHeight="cropHeight"
                        :fixedBox="false"
                        :centerBox="true"
                        :high="true"
                        @realTime="onCropRealTime">
                    </vue-cropper>
                </div>
                
                <!-- 右侧预览和操作 -->
                <div class="crop-sidebar">
                    <div class="crop-preview">
                        <div class="preview-title">预览效果</div>
                        <div class="preview-box" :style="previewBoxStyle">
                            <img :src="cropPreviewUrl" v-if="cropPreviewUrl" />
                            <div v-else class="preview-placeholder">请圈选区域</div>
                        </div>
                    </div>
                    
                    <div class="crop-actions">
                        <el-button size="small" @click="rotateLeft">
                            <i class="el-icon-refresh-left"></i> 左旋
                        </el-button>
                        <el-button size="small" @click="rotateRight">
                            <i class="el-icon-refresh-right"></i> 右旋
                        </el-button>
                        <el-button size="small" @click="resetCrop">
                            <i class="el-icon-refresh"></i> 重置
                        </el-button>
                    </div>
                    
                    <div class="crop-confirm">
                        <el-button @click="cropDialogVisible = false">取消</el-button>
                        <el-button type="primary" @click="confirmCrop">确认圈选</el-button>
                    </div>
                </div>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import { VueCropper } from 'vue-cropper';
import { ReplaceImageHttp, GetFileNameByPath } from "@/utils/comm.js";

export default {
    name: "UploadImages",
    components: {
        VueCropper
    },
    
    // ==================== Props ====================
    props: {
        // v-model 绑定值（图片URL，多个用逗号分隔）
        value: { 
            type: [Number, String], 
            default: '' 
        },
        
        // 最大上传数量
        limit: {
            type: Number,
            default: 1,
        },
        
        // 是否允许多选
        multiple: {
            type: Boolean,
            default: true,
        },
        
        // 允许的文件类型
        acceptTypes: {
            type: String,
            default: '.jpg,.png,.jpeg,.gif,.webp'
        },
        
        // 最大文件大小（MB）
        maxSize: {
            type: Number,
            default: 5,
        },
        
        // 是否显示圈选功能
        showCrop: {
            type: Boolean,
            default: true,
        },
        
        // 是否为头像模式（圆形预览）
        isAvatar: {
            type: Boolean,
            default: false,
        },
        
        // 卡片大小
        cardSize: {
            type: Number,
            default: 148,
        },
        
        // 裁剪框宽度
        cropWidth: {
            type: Number,
            default: 400,
        },
        
        // 裁剪框高度
        cropHeight: {
            type: Number,
            default: 400,
        },
        
        // 是否为圆形裁剪
        isCircleCrop: {
            type: Boolean,
            default: false,
        }
    },
    
    // ==================== Data ====================
    data() {
        return {
            uploadUrl: '/api/File/BatchUpload',
            uploadHeaders: {},
            uploadFileList: [],
            fileList: [],
            
            // 预览
            previewVisible: false,
            previewUrl: '',
            
            // 圈选
            cropDialogVisible: false,
            cropImageUrl: '',
            cropPreviewUrl: '',
            cropCurrentIndex: -1,
            cropCurrentItem: null,
        };
    },
    
    // ==================== Computed ====================
    computed: {
        previewBoxStyle() {
            return {
                width: '150px',
                height: '150px',
                borderRadius: this.isCircleCrop ? '50%' : '6px'
            };
        }
    },
    
        // ==================== Watch ====================
        watch: {
        value: {
            handler(newVal) {
                if (this._updating) return;
                
                if (newVal) {
                    const urls = String(newVal).split(",").filter(Boolean);
                    const newFileList = urls.map(x => {
                        // ✅ 如果是 Base64（旧数据），自动上传
                        if (x.startsWith('data:image')) {
                            this.autoUploadBase64(x);
                            return null;
                        }
                        const url = ReplaceImageHttp(x);
                        return {
                            url: url,
                            name: GetFileNameByPath(url) || '图片',
                            status: "success"
                        };
                    }).filter(Boolean);
                    
                    if (JSON.stringify(this.fileList.map(i => i.url)) !== JSON.stringify(newFileList.map(i => i.url))) {
                        this.fileList = newFileList;
                    }
                } else {
                    this.fileList = [];
                }
            },
            immediate: true
        }
    },
    
   methods: {
    // 上传前验证
    beforeUpload(file) {
    const isImage = file.type.startsWith('image/');
    const isLtMax = file.size / 1024 / 1024 < this.maxSize;
    
    if (!isImage) {
        this.$message.error(`只能上传图片文件！`);
        return false;
    }
    if (!isLtMax) {
        this.$message.error(`图片大小不能超过 ${this.maxSize}MB！`);
        return false;
    }
    
    // ✅ 如果 limit 为 1，上传前删除已有的图片
    if (this.limit === 1 && this.fileList.length > 0) {
        this.fileList = [];
        this.updateValue();
    }
    
    return true;
    },
    /**
     * Base64 转 File 对象
     * @param {string} dataURL - Base64 数据 (data:image/png;base64,xxx)
     * @param {string} filename - 文件名
     * @returns {File} File 对象
     */
    dataURLtoFile(dataURL, filename) {
        const arr = dataURL.split(',');
        const mime = arr[0].match(/:(.*?);/)[1];
        const bstr = atob(arr[1]);
        let n = bstr.length;
        const u8arr = new Uint8Array(n);
        while (n--) {
            u8arr[n] = bstr.charCodeAt(n);
        }
        return new File([u8arr], filename, { type: mime });
    },

    /**
     * Base64 转 Blob 对象
     * @param {string} dataURL - Base64 数据
     * @param {string} mimeType - MIME 类型
     * @returns {Blob} Blob 对象
     */
    dataURLtoBlob(dataURL, mimeType) {
        const arr = dataURL.split(',');
        const mime = mimeType || arr[0].match(/:(.*?);/)[1];
        const bstr = atob(arr[1]);
        let n = bstr.length;
        const u8arr = new Uint8Array(n);
        while (n--) {
            u8arr[n] = bstr.charCodeAt(n);
        }
        return new Blob([u8arr], { type: mime });
    },

    // 上传成功
    /**
 * 上传成功 - 自动打开裁剪
 */
    handleUploadSuccess(response, file) {
        if (response.Data && response.Data.length > 0) {
            const url = ReplaceImageHttp(response.Data[0].Url);
            const name = GetFileNameByPath(url) || file.name;
            
            // 检查是否已存在
            const exists = this.fileList.some(item => item.url === url);
            if (!exists) {
                // ✅ 先添加到列表，但标记为未裁剪
                const newItem = {
                    url: url,
                    name: name,
                    status: "success",
                    uid: file.uid || Date.now() + Math.random(),
                    isCropped: false  // 标记：未裁剪
                };
                this.fileList.push(newItem);
                this.updateValue();
                
                // ✅ 自动打开裁剪对话框
                const index = this.fileList.length - 1;
                this.$nextTick(() => {
                    this.openCropDialog(this.fileList[index], index);
                });
            }
        }
    },

    // 上传失败
    handleUploadError(error) {
        this.$message.error('上传失败，请重试！');
        console.error('上传失败:', error);
    },

    // 图片预览
    handlePreview(item) {
        if (!item || !item.url) {
            this.$message.warning('图片地址无效');
            return;
        }
        this.previewUrl = item.url;
        this.previewVisible = true;
    },

    // 删除图片
    handleRemove(item, index) {
        this.$confirm('确定要删除这张图片吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }).then(() => {
            this.fileList.splice(index, 1);
            this.updateValue();
            this.$message.success('删除成功！');
        }).catch(() => {});
    },

    // 更新 v-model
    /**
 * 更新 v-model 值
 */
    /**
 * 更新 v-model - 只发送 URL 路径
 */
    updateValue() {
        this._updating = true;
        // ✅ 所有图片都应该是 URL 路径
        const urls = this.fileList
            .map(item => item.url)
            .filter(url => url && !url.startsWith('data:image'));
        
        this.$emit('input', urls.join(','));
        
        this.$nextTick(() => {
            this._updating = false;
        });
    },
        /**
         * 打开圈选对话框
         */
        /**
 * 打开圈选对话框
 */
    openCropDialog(item, index) {
        // ✅ 如果是 Base64，直接使用；如果是 URL，添加时间戳防止缓存
        let imageUrl = item.url;
        if (imageUrl && !imageUrl.startsWith('data:image')) {
            // 如果是 URL 地址，添加时间戳防止缓存
            imageUrl = imageUrl + (imageUrl.includes('?') ? '&' : '?') + '_t=' + Date.now();
        }
        
        this.cropImageUrl = imageUrl;
        this.cropCurrentIndex = index;
        this.cropCurrentItem = item;
        this.cropDialogVisible = true;
        
        this.$nextTick(() => {
            if (this.$refs.cropper) {
                setTimeout(() => {
                    this.$refs.cropper.startCrop();
                }, 300);
            }
        });
        },
        /**
 * 图片加载失败处理
 */
        handleImageError(item, index) {
            console.warn('图片加载失败:', item.url);
            // 如果是 Base64 加载失败，可能是数据不完整
            if (item.url && item.url.startsWith('data:image')) {
                this.$message.warning('图片数据异常，请重新上传');
                // 移除该图片
                this.fileList.splice(index, 1);
                this.updateValue();
            }
        },

        /**
         * 关闭圈选对话框
         */
        closeCropDialog() {
            this.cropImageUrl = '';
            this.cropPreviewUrl = '';
        },

        /**
         * 圈选实时预览
         */
        onCropRealTime(data) {
            this.cropPreviewUrl = data.url;
        },

        /**
         * 左旋转
         */
        rotateLeft() {
            if (this.$refs.cropper) {
                this.$refs.cropper.rotateLeft();
            }
        },

        /**
         * 右旋转
         */
        rotateRight() {
            if (this.$refs.cropper) {
                this.$refs.cropper.rotateRight();
            }
        },

        /**
         * 重置裁剪
         */
        resetCrop() {
            if (this.$refs.cropper) {
                this.$refs.cropper.startCrop();
            }
        },

        /**
         * 确认圈选
         */
                async confirmCrop() {
            if (!this.$refs.cropper) {
                this.$message.warning('请先上传图片');
                return;
            }
            
            try {
                // 显示加载
                this.$message({ message: '正在处理裁剪...', type: 'info', duration: 0 });
                
                // 1. 获取裁剪数据
                const cropData = await new Promise((resolve) => {
                    this.$refs.cropper.getCropData((data) => resolve(data));
                });
                
                // 2. 转为文件
                const file = this.dataURLtoFile(cropData, 'crop_' + Date.now() + '.png');
                
                // 3. 上传到后端
                const formData = new FormData();
                formData.append('file', file);
                const response = await this.$Post('/File/BatchUpload', formData);
                
                this.$message.closeAll();
                
                if (response.Success && response.Data?.length > 0) {
                    const url = ReplaceImageHttp(response.Data[0].Url);
                    
                    // 4. 更新列表
                    const cropItem = {
                        url: url,
                        name: '裁剪_' + (this.cropCurrentItem?.name || '图片'),
                        status: "success",
                        isCrop: true
                    };
                    
                    if (this.cropCurrentIndex !== -1) {
                        this.fileList.splice(this.cropCurrentIndex, 1, cropItem);
                    } else {
                        this.fileList.push(cropItem);
                    }
                    
                    this.updateValue();
                    this.cropDialogVisible = false;
                    this.$message.success('圈选成功！');
                    
                    this.$emit('crop-confirm', {
                        url: url,
                        index: this.cropCurrentIndex,
                        item: cropItem
                    });
                } else {
                    this.$message.error('裁剪图片上传失败，请重试');
                }
                
            } catch (error) {
                this.$message.closeAll();
                console.error('裁剪失败:', error);
                this.$message.error('裁剪失败，请重试');
            }
        },

        /**
         * 获取文件列表（供父组件调用）
         */
        getFileList() {
            return this.fileList;
        },

        /**
         * 清空文件列表
         */
        clearFiles() {
            this.fileList = [];
            this.updateValue();
        }
    }
};
</script>

<style scoped>
/* ========================================
   1. 容器
   ======================================== */
.upload-images-wrapper {
    width: 100%;
}

/* ========================================
   2. 上传区域
   ======================================== */
/* 上传按钮修复 */
/* ========================================
   1. 上传区域
   ======================================== */
.upload-area {
    display: flex !important;
    flex-wrap: wrap !important;  /* ✅ 允许换行 */
    align-items: flex-start !important;
    gap: 12px !important;
}

/* ========================================
   2. 上传按钮 - 小巧美观
   ======================================== */
.upload-btn {
    flex-shrink: 0;
}

/* 🔧 核心样式：减小尺寸，美化外观 */
.upload-btn ::v-deep .el-upload--picture-card {
    width: 80px !important;   /* ✅ 从 148px 减小到 80px */
    height: 80px !important;  /* ✅ 从 148px 减小到 80px */
    display: flex !important;
    align-items: center !important;
    justify-content: center !important;
    background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%) !important;
    border: 2px dashed #c0c4cc !important;
    border-radius: 8px !important;
    transition: all 0.3s ease !important;
    cursor: pointer !important;
    margin: 0 !important;
    padding: 0 !important;
    line-height: normal !important;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04) !important;
}

.upload-btn ::v-deep .el-upload--picture-card:hover {
    border-color: #409EFF !important;
    background: linear-gradient(135deg, #ecf5ff 0%, #d9ecff 100%) !important;
    transform: translateY(-1px) !important;
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2) !important;
}

/* 自定义触发器 */
.upload-trigger {
    display: flex !important;
    flex-direction: column !important;
    align-items: center !important;
    justify-content: center !important;
    width: 100% !important;
    height: 100% !important;
    gap: 2px;
}

/* 加号图标 */
.upload-trigger .el-icon-plus {
    font-size: 22px !important;  /* ✅ 减小图标 */
    color: #8c939d !important;
    font-weight: 300 !important;
    transition: color 0.3s ease !important;
}

.upload-btn ::v-deep .el-upload--picture-card:hover .upload-trigger .el-icon-plus {
    color: #409EFF !important;
}

/* 文字提示 */
.upload-trigger .upload-tip {
    font-size: 11px !important;  /* ✅ 减小文字 */
    color: #999 !important;
    line-height: 1.2 !important;
    letter-spacing: 0.5px;
}

/* 隐藏默认加号 */
.upload-btn ::v-deep .el-upload--picture-card .el-icon-plus {
    display: none !important;
}


/* ========================================
   5. 空状态 - 适配
   ======================================== */
.empty-state {
    padding: 20px 0;
}

.empty-state i {
    font-size: 32px;
}

.empty-state p {
    font-size: 12px;
    margin-top: 6px;
}

.upload-info {
    flex: 1;
    padding-top: 8px;
}

.tip-text {
    font-size: 12px;
    color: #999;
}

.tip-text i {
    margin-right: 4px;
    color: #409EFF;
}

/* ========================================
   3. 图片列表
   ======================================== */
/* 图片列表 - 强制换行到下一行 */
.image-list {
    display: flex !important;
    flex-wrap: wrap !important;
    gap: 12px !important;
    margin-top: 12px !important;
    width: 100% !important;
    flex-basis: 100% !important;  /* ✅ 强制占满一行 */
}

.image-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 6px;
}

.image-item.is-avatar .image-card {
    border-radius: 50%;
    overflow: hidden;
}

.image-card {
    position: relative;
    width: v-bind(cardSize + 'px');
    height: v-bind(cardSize + 'px');
    border-radius: 6px;
    overflow: hidden;
    border: 1px solid #e4e7ed;
    background: #f5f7fa;
    cursor: pointer;
    transition: all 0.3s ease;
}

.image-card:hover {
    border-color: #409EFF;
    box-shadow: 0 2px 12px rgba(64, 158, 255, 0.3);
}

.image-card img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    object-position: center;
}

/* 序号角标 */
.image-index {
    position: absolute;
    top: 4px;
    left: 4px;
    background: rgba(0, 0, 0, 0.6);
    color: #fff;
    font-size: 12px;
    padding: 0 8px;
    border-radius: 10px;
    line-height: 20px;
}

/* 操作按钮 - 悬浮显示 */
.image-actions {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    background: rgba(0, 0, 0, 0.5);
    opacity: 0;
    transition: opacity 0.3s ease;
    border-radius: inherit;
}

.image-card:hover .image-actions {
    opacity: 1;
}

.image-actions .el-button {
    width: 32px;
    height: 32px;
    padding: 0;
    font-size: 14px;
    transform: scale(0.8);
    transition: transform 0.3s ease;
}

.image-card:hover .image-actions .el-button {
    transform: scale(1);
}

.image-actions .el-button--primary {
    background-color: #409EFF;
    border-color: #409EFF;
}

.image-actions .el-button--danger {
    background-color: #f56c6c;
    border-color: #f56c6c;
}

.image-actions .el-button--info {
    background-color: #909399;
    border-color: #909399;
}

.image-name {
    font-size: 12px;
    color: #666;
    max-width: v-bind(cardSize + 'px');
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    text-align: center;
}

/* ========================================
   4. 空状态
   ======================================== */
.empty-state {
    padding: 40px 0;
    text-align: center;
    color: #999;
}

.empty-state i {
    font-size: 48px;
    color: #ddd;
}

.empty-state p {
    margin-top: 10px;
    font-size: 14px;
}

/* ========================================
   5. 预览对话框
   ======================================== */
.preview-dialog ::v-deep .el-dialog__body {
    padding: 0;
    max-height: 70vh;
    display: flex;
    align-items: center;
    justify-content: center;
}

.preview-dialog ::v-deep .el-dialog__body img {
    max-height: 70vh;
    object-fit: contain;
}

/* ========================================
   6. 圈选对话框
   ======================================== */
.crop-dialog ::v-deep .el-dialog__body {
    padding: 20px;
}

.crop-container {
    display: flex;
    gap: 30px;
    flex-wrap: wrap;
    min-height: 400px;
}

.crop-wrapper {
    flex: 1;
    min-width: 400px;
    min-height: 400px;
    background: #f5f7fa;
    border-radius: 6px;
    overflow: hidden;
}

/* VueCropper 样式覆盖 */
.crop-wrapper ::v-deep .cropper-box {
    width: 100% !important;
    height: 100% !important;
}

.crop-wrapper ::v-deep .cropper-line {
    border-color: #409EFF !important;
}

.crop-wrapper ::v-deep .cropper-point {
    background-color: #409EFF !important;
    border-color: #409EFF !important;
}

.crop-sidebar {
    width: 220px;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 15px;
}

.crop-preview {
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.preview-title {
    font-size: 14px;
    font-weight: 600;
    color: #333;
    margin-bottom: 10px;
}

.preview-box {
    border: 2px dashed #d9d9d9;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
    background: #fafafa;
}

.preview-box img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.preview-placeholder {
    color: #bbb;
    font-size: 12px;
}

.crop-actions {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    justify-content: center;
    width: 100%;
}

.crop-actions .el-button {
    margin: 0;
    font-size: 12px;
}

.crop-confirm {
    display: flex;
    gap: 10px;
    width: 100%;
    padding-top: 10px;
    border-top: 1px solid #e4e7ed;
    justify-content: center;
}

/* ========================================
   7. 响应式
   ======================================== */
@media (max-width: 768px) {
    .upload-area {
        flex-direction: column;
        align-items: center;
    }
    
    .upload-info {
        text-align: center;
    }
    
    .crop-container {
        flex-direction: column;
        align-items: center;
    }
    
    .crop-wrapper {
        min-width: 300px;
        min-height: 300px;
        width: 100%;
    }
    
    .crop-sidebar {
        width: 100%;
        max-width: 350px;
    }
}
</style>
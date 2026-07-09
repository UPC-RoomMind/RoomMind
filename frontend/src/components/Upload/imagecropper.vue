<template>
    <div class="image-cropper-container">
        <!-- 上传按钮 -->
        <el-upload
            class="upload-btn"
            :action="uploadUrl"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            accept=".jpg,.png,.jpeg"
            :before-upload="beforeUpload">
            <el-button type="primary" size="small">
                <i class="el-icon-upload"></i> 选择图片
            </el-button>
        </el-upload>

        <!-- 裁剪区域 -->
        <div v-if="imageUrl" class="cropper-wrapper">
            <div class="cropper-container">
                <vue-cropper
                    ref="cropper"
                    :img="imageUrl"
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
                    @realTime="realTime">
                </vue-cropper>
            </div>

            <!-- 预览 -->
            <div class="preview-wrapper">
                <div class="preview-title">预览</div>
                <div class="preview-box">
                    <img :src="previewUrl" v-if="previewUrl" />
                    <div v-else class="preview-placeholder">请圈选区域</div>
                </div>
                
                <div class="crop-actions">
                    <el-button size="small" @click="rotateLeft">左旋</el-button>
                    <el-button size="small" @click="rotateRight">右旋</el-button>
                    <el-button size="small" @click="resetCrop">重置</el-button>
                    <el-button type="primary" size="small" @click="confirmCrop">确认</el-button>
                </div>
            </div>
        </div>

        <!-- 空状态 -->
        <div v-else class="empty-tip">
            <i class="el-icon-picture-outline"></i>
            <p>请上传图片进行圈选</p>
        </div>
    </div>
</template>

<script>
import { VueCropper } from 'vue-cropper';
import { ReplaceImageHttp } from "@/utils/comm.js";

export default {
    name: "ImageCropper",
    components: {
        VueCropper
    },
    props: {
        value: { type: String, default: '' },
        cropWidth: { type: Number, default: 400 },
        cropHeight: { type: Number, default: 400 },
        isCircle: { type: Boolean, default: false }
    },
    data() {
        return {
            uploadUrl: process.env.VUE_APP_BASE_API + "/File/BatchUpload",
            imageUrl: '',
            previewUrl: ''
        };
    },
    mounted() {
        if (this.value) {
            this.imageUrl = ReplaceImageHttp(this.value);
        }
    },
    methods: {
        beforeUpload(file) {
            const isImage = file.type.startsWith('image/');
            const isLt5M = file.size / 1024 / 1024 < 5;
            if (!isImage) {
                this.$message.error('只能上传图片文件！');
                return false;
            }
            if (!isLt5M) {
                this.$message.error('图片大小不能超过 5MB！');
                return false;
            }
            return true;
        },

        handleUploadSuccess(response) {
            if (response.Data && response.Data.length > 0) {
                this.imageUrl = ReplaceImageHttp(response.Data[0].Url);
                this.$message.success('上传成功，请圈选展示区域');
            }
        },

        realTime(data) {
            this.previewUrl = data.url;
        },

        rotateLeft() {
            this.$refs.cropper.rotateLeft();
        },

        rotateRight() {
            this.$refs.cropper.rotateRight();
        },

        resetCrop() {
            this.$refs.cropper.startCrop();
        },

        confirmCrop() {
            if (!this.$refs.cropper) {
                this.$message.warning('请先上传图片');
                return;
            }
            this.$refs.cropper.getCropData((data) => {
                this.$emit('input', data);
                this.$emit('confirm', data);
                this.$message.success('圈选成功！');
            });
        }
    }
};
</script>

<style scoped>
.image-cropper-container {
    width: 100%;
    padding: 20px;
    background: #fff;
    border-radius: 8px;
    border: 1px solid #e4e7ed;
}

.upload-btn {
    margin-bottom: 20px;
}

.empty-tip {
    text-align: center;
    padding: 40px 0;
    color: #999;
}

.empty-tip i {
    font-size: 48px;
    color: #ddd;
}

.cropper-wrapper {
    display: flex;
    gap: 30px;
    flex-wrap: wrap;
}

.cropper-container {
    flex: 1;
    min-width: 350px;
    min-height: 350px;
    background: #f5f7fa;
    border-radius: 6px;
    overflow: hidden;
}

/* VueCropper 样式覆盖 */
.cropper-container ::v-deep .cropper-line {
    border-color: #409EFF !important;
}

.cropper-container ::v-deep .cropper-point {
    background-color: #409EFF !important;
    border-color: #409EFF !important;
}

.preview-wrapper {
    width: 200px;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.preview-title {
    font-size: 14px;
    font-weight: 600;
    margin-bottom: 10px;
}

.preview-box {
    width: 150px;
    height: 150px;
    border: 2px dashed #d9d9d9;
    border-radius: 6px;
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
    margin-top: 15px;
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    justify-content: center;
}

.crop-actions .el-button {
    margin: 0;
}
</style>
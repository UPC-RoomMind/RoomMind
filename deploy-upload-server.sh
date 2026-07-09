#!/bin/bash

echo "=================================="
echo "  部署远程上传服务"
echo "=================================="

# 检查Python是否安装
if ! command -v python3 &> /dev/null; then
    echo "正在安装Python3..."
    apt update && apt install python3 python3-pip -y
fi

# 检查Flask是否安装
if ! python3 -c "import flask" &> /dev/null; then
    echo "正在安装Flask..."
    pip3 install flask
fi

# 创建目录
mkdir -p /root/roommind-dev/roommind-dev-upload
mkdir -p /opt/upload-server

# 上传Python脚本
echo "正在上传上传服务脚本..."
cat > /opt/upload-server/upload-server.py << 'EOF'
#!/usr/bin/env python3
import os
import json
from flask import Flask, request, jsonify

app = Flask(__name__)

UPLOAD_DIR = '/root/roommind-dev/roommind-dev-upload/'

@app.route('/upload', methods=['POST'])
def upload_file():
    if 'file' not in request.files:
        return jsonify({'code': -1, 'message': 'No file part'}), 400

    file = request.files['file']
    if file.filename == '':
        return jsonify({'code': -1, 'message': 'No selected file'}), 400

    user_id = request.form.get('userId', '0')
    time_str = request.form.get('time', '')
    
    if not time_str:
        return jsonify({'code': -1, 'message': 'Time is required'}), 400

    save_dir = os.path.join(UPLOAD_DIR, user_id, time_str)
    os.makedirs(save_dir, exist_ok=True)

    file_path = os.path.join(save_dir, file.filename)
    file.save(file_path)

    return jsonify({
        'code': 0,
        'message': 'Upload success',
        'path': f'{user_id}/{time_str}/{file.filename}'
    })

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8888, debug=False)
EOF

chmod +x /opt/upload-server/upload-server.py

# 创建systemd服务
echo "正在创建systemd服务..."
cat > /etc/systemd/system/upload-server.service << 'EOF'
[Unit]
Description=RoomMind Upload Server
After=network.target

[Service]
Type=simple
User=root
WorkingDirectory=/opt/upload-server
ExecStart=/usr/bin/python3 /opt/upload-server/upload-server.py
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
EOF

# 启动服务
echo "正在启动上传服务..."
systemctl daemon-reload
systemctl enable upload-server
systemctl start upload-server

# 检查状态
sleep 2
echo "=================================="
echo "  上传服务状态"
echo "=================================="
systemctl status upload-server --no-pager

echo ""
echo "=================================="
echo "  部署完成"
echo "=================================="
echo "上传服务地址: http://服务器IP:8888/upload"
echo "上传文件目录: /root/roommind-dev/roommind-dev-upload/"
echo "查看日志: journalctl -u upload-server -f"
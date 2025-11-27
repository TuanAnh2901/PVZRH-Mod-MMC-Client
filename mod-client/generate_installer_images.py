import os
from PIL import Image, ImageDraw, ImageFont

# ================= 配置区域 =================
# 颜色配置 (RGB) - 这是一个深色游戏风格配色
COLOR_BG_TOP = (30, 30, 40)      # 深蓝灰
COLOR_BG_BOTTOM = (20, 20, 25)   # 接近黑
COLOR_ACCENT = (0, 122, 204)     # 科技蓝 (线条颜色)
COLOR_TEXT = (240, 240, 240)     # 白色文字

# 输出目录 (会自动创建)
OUTPUT_DIR = "src-tauri/installer"
# ===========================================

def create_gradient(width, height, top_color, bottom_color):
    """创建一个垂直渐变的基础图像"""
    base = Image.new('RGB', (width, height), top_color)
    top_r, top_g, top_b = top_color
    bot_r, bot_g, bot_b = bottom_color

    for y in range(height):
        r = int(top_r + (bot_r - top_r) * y / height)
        g = int(top_g + (bot_g - top_g) * y / height)
        b = int(top_b + (bot_b - top_b) * y / height)
        ImageDraw.Draw(base).line([(0, y), (width, y)], fill=(r, g, b))
    return base

def draw_tech_pattern(draw, width, height):
    """画一些简单的科技感线条"""
    # 底部横线
    draw.line([(0, height - 10), (width, height - 10)], fill=COLOR_ACCENT, width=2)
    # 侧边装饰
    draw.line([(10, 10), (10, height-20)], fill=COLOR_ACCENT, width=1)
    
def generate_header():
    """生成顶部图片 (建议尺寸 150x57)"""
    w, h = 150, 57
    img = create_gradient(w, h, COLOR_BG_TOP, COLOR_BG_BOTTOM)
    draw = ImageDraw.Draw(img)
    
    # 画线条装饰
    draw.line([(0, h-2), (w, h-2)], fill=COLOR_ACCENT, width=2)
    
    # 尝试加载默认字体，如果失败则使用默认
    try:
        # Windows 常用字体路径
        font = ImageFont.truetype("arial.ttf", 16)
    except:
        font = ImageFont.load_default()

    # 绘制文字
    text = "Mod Manager"
    # 获取文字大小 (兼容旧版 Pillow)
    bbox = draw.textbbox((0, 0), text, font=font)
    text_w = bbox[2] - bbox[0]
    text_h = bbox[3] - bbox[1]
    
    # 居中显示
    draw.text(((w - text_w) / 2, (h - text_h) / 2), text, font=font, fill=COLOR_TEXT)
    
    save_path = os.path.join(OUTPUT_DIR, "header.bmp")
    img.save(save_path)
    print(f"Generated: {save_path}")

def generate_sidebar():
    """生成侧边图片 (建议尺寸 164x314)"""
    w, h = 164, 314
    img = create_gradient(w, h, COLOR_BG_TOP, COLOR_BG_BOTTOM)
    draw = ImageDraw.Draw(img)
    
    # 画装饰
    draw_tech_pattern(draw, w, h)
    
    try:
        font_title = ImageFont.truetype("arialbd.ttf", 20) # 粗体
        font_sub = ImageFont.truetype("arial.ttf", 12)
    except:
        font_title = ImageFont.load_default()
        font_sub = ImageFont.load_default()

    # 绘制标题
    draw.text((20, 30), "Mod", font=font_title, fill=COLOR_TEXT)
    draw.text((20, 55), "Manager", font=font_title, fill=COLOR_TEXT)
    
    # 绘制版本号或其他信息
    draw.text((20, 280), "Installer", font=font_sub, fill=(150, 150, 150))

    save_path = os.path.join(OUTPUT_DIR, "sidebar.bmp")
    img.save(save_path)
    print(f"Generated: {save_path}")

if __name__ == "__main__":
    if not os.path.exists(OUTPUT_DIR):
        os.makedirs(OUTPUT_DIR)
        print(f"Created directory: {OUTPUT_DIR}")
        
    generate_header()
    generate_sidebar()
    print("完成！请检查 src-tauri/installer 目录。")
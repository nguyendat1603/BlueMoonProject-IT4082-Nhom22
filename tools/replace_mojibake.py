from pathlib import Path
import re

MAPPINGS = {
    "Biáº¿n Ä‘á»™ng dÃ¢n cÆ° theo thÃ¡ng": "Biến động dân cư theo tháng",
    "Táº§ng": "Tầng",
    "Sá»‘ nhÃ ": "Số nhà",
    "Diá»‡n tÃ­ch": "Diện tích",
    "Chá»§ sá»Ÿ há»¯u": "Chủ sở hữu",
    "Tráº¡ng thÃ¡i": "Trạng thái",
    "Sá»‘ káº¿t quáº£ hiá»ƒn thá»‹": "Số kết quả hiển thị",
    "TÃ¬m kiáº¿m": "Tìm kiếm",
    "Sá»‘ Ä‘iá»‡n thoáº¡i": "Số điện thoại",
    "NgÃ y chuyá»ƒn Ä‘áº¿n": "Ngày chuyển đến",
    "Táº¡o hÃ³a Ä‘Æ¡n": "Tạo hóa đơn",
    "TÃªn khoáº£n thu": "Tên khoản thu",
    "Loáº¡i khoáº£n thu": "Loại khoản thu",
    "Bá»™ pháº­n quáº£n lÃ½": "Bộ phận quản lý",
    "NgÃ y ná»™p": "Ngày nộp",
    "NgÃ y táº¡o": "Ngày tạo",
    "NgÃ y cáº­p nháº­t": "Ngày cập nhật",
    "MÃ£ cÄƒn há»™": "Mã căn hộ",
    "MÃ£ Ä‘á»‹nh danh": "Mã định danh",
    "MÃ£ khoáº£n thu": "Mã khoản thu",
    "MÃ£ hÃ³a Ä‘Æ¡n": "Mã hóa đơn",
    "Nháº­p": "Nhập",
    "Nháº­p mÃ£": "Nhập mã",
    "Nháº­p tÃªn": "Nhập tên",
    "Nháº­p bá»™ pháº­n": "Nhập bộ phận",
    "Giá»›i tÃ­nh": "Giới tính",
    "Há» vÃ  tÃªn": "Họ và tên",
    "Nháº­p há» vÃ  tÃªn": "Nhập họ và tên",
    "Báº¯t buá»™c": "Bắt buộc",
    "Sá»‘ tiá»n": "Số tiền",
    "NgÃ y sinh": "Ngày sinh",
    "NgÃ ": "Ng",  # fallback
}

def fix_file(path: Path):
    text = path.read_text(encoding='utf-8', errors='replace')
    orig = text
    for bad, good in MAPPINGS.items():
        if bad in text:
            text = text.replace(bad, good)
    # also fix common mojibake sequences with regex
    text = re.sub(r'\\s+Nháº­p', ' Nhập', text)
    if text != orig:
        bak = path.with_suffix(path.suffix + '.bak2')
        path.write_text(text, encoding='utf-8')
        print(f'FIXED: {path}')
    else:
        print(f'OK: {path}')

def main():
    root = Path('src') / 'main' / 'resources' / 'view'
    files = list(root.rglob('*.fxml')) if root.exists() else []
    for f in files:
        fix_file(f)

if __name__ == '__main__':
    main()



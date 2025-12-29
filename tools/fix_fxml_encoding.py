import sys
from pathlib import Path

def fix_file(path: Path) -> bool:
    try:
        b = path.read_bytes()
        # remove UTF-8 BOM if present
        if b.startswith(b'\xef\xbb\xbf'):
            b = b[3:]
        try:
            text = b.decode('utf-8')
        except Exception:
            # fallback: interpret as latin1 then decode to utf-8
            text = b.decode('latin-1')

        # heuristic: detect mojibake sequences typical for Vietnamese (Ã, Ä, â etc.)
        if any(seq in text for seq in ('Ã', 'Ä', 'â', 'á»', 'Ä©', 'Ã£', 'Ãª')):
            try:
                fixed = text.encode('latin-1').decode('utf-8')
            except Exception:
                # if re-decoding fails, skip
                return False
            # write back as UTF-8 without BOM
            path.write_text(fixed, encoding='utf-8')
            print(f"FIXED: {path}")
            return True
        else:
            # ensure file is valid UTF-8 and rewrite without BOM
            path.write_text(text, encoding='utf-8')
            # not counted as fixed but normalized
            print(f"OK: {path}")
            return False
    except Exception as e:
        print(f"ERR {path}: {e}")
        return False

def main():
    repo_root = Path('.').resolve()
    src_dir = repo_root / 'src' / 'main' / 'resources' / 'view'
    target_dir = repo_root / 'target' / 'classes' / 'view'
    files = []
    if src_dir.exists():
        files += list(src_dir.rglob('*.fxml'))
    if target_dir.exists():
        files += list(target_dir.rglob('*.fxml'))

    if not files:
        print("No .fxml files found")
        return

    fixed_count = 0
    for f in files:
        if fix_file(f):
            fixed_count += 1

    print(f"Completed. Files inspected: {len(files)}. Files fixed: {fixed_count}")

if __name__ == '__main__':
    main()



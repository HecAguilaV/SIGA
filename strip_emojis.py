import os
import re

def remove_emojis(text):
    # Patrón exhaustivo para capturar bloques Unicode de Emojis, Dingbats y Símbolos
    emoji_pattern = re.compile(
        "["
        u"\U0001F600-\U0001F64F"  # emoticons
        u"\U0001F300-\U0001F5FF"  # symbols & pictographs
        u"\U0001F680-\U0001F6FF"  # transport & map symbols
        u"\U0001F700-\U0001F77F"  # alchemical symbols
        u"\U0001F780-\U0001F7FF"  # Geometric Shapes Extended
        u"\U0001F800-\U0001F8FF"  # Supplemental Arrows-C
        u"\U0001F900-\U0001F9FF"  # Supplemental Symbols and Pictographs
        u"\U0001FA00-\U0001FA6F"  # Chess Symbols
        u"\U0001FA70-\U0001FAFF"  # Symbols and Pictographs Extended-A
        u"\u2600-\u26FF"          # Miscellaneous Symbols
        u"\u2700-\u27BF"          # Dingbats
        u"\u2300-\u23FF"          # Miscellaneous Technical
        u"\u2B50-\u2B50"          # Star
        u"\u25A0-\u25FF"          # Geometric Shapes (some icons use this)
        u"\u2190-\u21FF"          # Arrows (sometimes used as icons)
        "]+", flags=re.UNICODE)
    return emoji_pattern.sub(r'', text)

def process_directory(directory):
    for root, dirs, files in os.walk(directory):
        if 'node_modules' in root or '.git' in root or 'lab' in root or '.expo' in root:
            continue
        for file in files:
            if file.endswith('.md') or file.endswith('.code-workspace'):
                filepath = os.path.join(root, file)
                try:
                    with open(filepath, 'r', encoding='utf-8') as f:
                        content = f.read()
                    
                    new_content = remove_emojis(content)
                    
                    if new_content != content:
                        with open(filepath, 'w', encoding='utf-8') as f:
                            f.write(new_content)
                        print(f"Limpiado: {filepath}")
                except Exception as e:
                    print(f"Error en {filepath}: {e}")

if __name__ == "__main__":
    process_directory(".")

"""
Script para generar ejemplos representativos para quantization TFLite.
Ajusta la ruta a tus imágenes y el tamaño de entrada del modelo.
"""
import os
from PIL import Image
import numpy as np

dataset_dir = '../dataset/images/val'
image_files = [os.path.join(dataset_dir, f) for f in os.listdir(dataset_dir) if f.lower().endswith(('.jpg', '.jpeg', '.png'))][:100]


def representative_dataset_gen():
    for img_path in image_files:
        img = Image.open(img_path).convert('RGB').resize((640, 640))
        arr = np.array(img).astype(np.float32) / 255.0
        arr = np.expand_dims(arr, axis=0)
        yield [arr]


if __name__ == '__main__':
    print(f'Representative dataset listo con {len(image_files)} imágenes.')

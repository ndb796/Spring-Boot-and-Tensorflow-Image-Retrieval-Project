import glob
import os
import pickle

from PIL import Image
from utility.extractor import FeatureExtractor

extractor = FeatureExtractor()

# 폴더에 저장되어 있는 이미지를 이름 순으로 접근합니다.
for img_path in sorted(glob.glob('store/img/*.jpg')):
    # 이미지 객체를 생성합니다.
    img = Image.open(img_path)

    # 이미지에서 특징을 추출합니다.
    print(img_path + "을(를) 처리중입니다.")
    feature = extractor.extract(img)

    # 추출된 특징 객체를 .pkl 파일로 저장합니다.
    featurePath = 'store/feature/' + os.path.splitext(os.path.basename(img_path))[0] + '.pkl'
    pickle.dump(feature, open(featurePath, 'wb'))
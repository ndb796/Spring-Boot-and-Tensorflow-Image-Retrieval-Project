import os
import numpy as np
import json
import glob
import pickle

from PIL import Image
from utility.extractor import FeatureExtractor
from datetime import datetime
from flask import Flask, request, render_template

app = Flask(__name__)

# 존재하는 모든 이미지의 특징 정보를 불러옵니다.
features = []
img_paths = []

for feature_path in glob.glob("store\\feature\\*"):
    features.append(pickle.load(open(feature_path, 'rb')))
    img_paths.append('store\\img\\' + os.path.splitext(os.path.basename(feature_path))[0] + '.jpg')

extractor = FeatureExtractor()

@app.route('/', methods=['GET', 'POST'])
def index():
    if request.method == "GET":
        return render_template("index.html")
    else:
        # 사용자가 전송한 이미지 데이터를 전달 받습니다.
        file = request.files['query_img']
        img = Image.open(file.stream)

        # 사용자의 이미지에서 특징을 추출합니다.
        query = extractor.extract(img)

        # 특징이 유사한 가까운 순으로 정렬 기준을 설정합니다.
        dists = np.linalg.norm(features - query, axis=1)

        # 가장 유사한 이미지 10개를 추출하여 JSON 형식으로 반환합니다.
        ids = np.argsort(dists)[:10] # Top 30 results
        result = {
            "scores": [
                ({"id": str(dists[id]), "path": img_paths[id]}) for id in ids
            ]
        }
        return json.dumps(result);

if __name__=="__main__":
    app.run("0.0.0.0")
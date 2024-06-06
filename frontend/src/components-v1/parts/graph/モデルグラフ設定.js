import { ModelLineGraphOptions } from "@/components-v1/parts/graph/モデル線グラフオプション.vue";
import { GraphAnnotation } from "@/components-v1/parts/graph/グラフアノテーション.vue";
import { ModelDataSeries } from "@/components-v1/parts/graph/モデルグラフデータ.vue";
import { GraphPanelDataDTO } from "@/components-v1/parts/graph/グラフパネルデータDTO";

export class ModelGraphSettings {
    constructor(parent) {
        this.optionsMapper = new ModelLineGraphOptions();
        this.annotationMapper = new GraphAnnotation();
        this.ModelDataSeries = new ModelDataSeries();
        this.optionsMapper.setLoadingParent(parent);
    }
    //* ============================================
    // タイトル設定
    //* ============================================
    #setTitles(title, ytitle) {
        // タイトル
        this.optionsMapper.setGraphTitle(title);
        // サブタイトル
        if (null != source.estimated) {
            this.optionsMapper.addSubTitleElement(ytitle, source.estimated);
            this.optionsMapper.createSubtitle();
        }
        // Y軸タイトル
        this.optionsMapper.setYScaleTitle(ytitle);
    }
    //* ============================================
    // アノテーションと目盛設定
    //* ============================================
    #setAnnotationAndScale(source) {
        // Y軸の最高値
        let max = source.yend;
        // アノテーション
        if ((source.annotations != null) && (source.annotations.length > 0)) {
            max = this.annotationMapper.setAnnotations(max, source.annotations);
        }
        this.optionsMapper.setXAnnotations(this.annotationMapper.getTodayAnnotation());
        this.optionsMapper.setYAnnotations(this.annotationMapper.getValueAnnotations())
        // カテゴリー
        this.optionsMapper.setXCategory(source.category);
        // 目盛
        this.optionsMapper.setYScale(max);
    }
    //* ============================================
    // データ設定
    //* ============================================
    #setData(values, predictValues, meauredValues) {
        this.ModelDataSeries.addDataSeries("実績値", values);
        this.ModelDataSeries.addDataSeries("推定値", predictValues);
        this.ModelDataSeries.addDataSeries("実測値", meauredValues);
    }
    //* ============================================
    // 生育推定グラフ設定
    //* ============================================
    setGrowthGraph(source) {
        this.#setTitles("生育ステージ推定モデル", "累積F値");
        this.#setAnnotationAndScale(source);
        this.#setData(source.values, source.predictValues, source.meauredValues);
    }
    //* ============================================
    // 葉面積推定グラフ設定
    //* ============================================
    setLeafAreaGraph(source) {
        this.#setTitles("葉面積推定モデル", "葉面積(㎡)");
        this.#setAnnotationAndScale(source);
        this.#setData(source.values, source.predictValues, source.meauredValues);
    }
    //* ============================================
    // 葉枚数推定グラフ設定
    //* ============================================
    setLeafCountGraph(source) {
        this.#setTitles("葉枚数推定モデル", "葉枚数(枚)");
        this.#setAnnotationAndScale(source);
        this.#setData(source.values, source.predictValues, source.meauredValues);
    }
    //* ============================================
    // 光合成推定グラフ設定
    //* ============================================
    setPhotoSynthesysGraph(source) {
        this.#setTitles("光合成推定モデル", "光合成量(kgCO2 vine^-1)");
        this.#setAnnotationAndScale(source);
        this.#setData(source.values, source.predictValues, source.meauredValues);
    }
}
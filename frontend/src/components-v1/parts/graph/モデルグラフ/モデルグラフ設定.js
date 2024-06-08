import { LineGraphOptions } from "@/components-v1/parts/graph/線グラフオプション.js";
import { GraphAnnotation } from "@/components-v1/parts/graph/グラフアノテーション.js";
import { GraphDataSeries } from "@/components-v1/parts/graph/グラフデータ.js";

export class ModelGraphSettings {
    constructor() {
        this.optionsMapper = new LineGraphOptions();
        this.annotationMapper = new GraphAnnotation();
        this.graphDataSeries = new GraphDataSeries();
    }
    //* ============================================
    // オブジェクトリファレンス
    //* ============================================
    getChartOptions() { return this.optionsMapper.getChartOptions(); }
    getSeries() { return this.graphDataSeries.getSeries(); }
    //* ============================================
    // タイトル設定
    //* ============================================
    #setTitles(source, title, ytitle) {
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
        this.graphDataSeries.addDataSeries("実績値", values);
        this.graphDataSeries.addDataSeries("推定値", predictValues);
        this.graphDataSeries.addDataSeries("実測値", meauredValues);
    }
    //* ============================================
    // 生育推定グラフ設定
    //* ============================================
    setGrowthGraph(source) {
        this.#setTitles(source, "生育ステージ推定モデル", "累積F値");
        this.#setAnnotationAndScale(source);
        this.#setData(source.values, source.predictValues, source.meauredValues);
    }
    //* ============================================
    // 葉面積・葉枚数推定グラフ設定
    //* ============================================
    setLeafAreaGraph(area, count) {
        // タイトル
        this.optionsMapper.setGraphTitle("葉面積と出葉枚");
        // サブタイトル
        let subtitleCount = 0;
        if (null != area.estimated) {
            this.optionsMapper.addSubTitleElement("葉面積(㎡)", area.estimated);
            subtitleCount++;
        }
        if (null != area.estimated) {
            this.optionsMapper.addSubTitleElement("枚", count.estimated);
            subtitleCount++;
        }
        if (subtitleCount > 0) this.optionsMapper.createSubtitle();
        // Y軸複合タイトル
        this.optionsMapper.setYScaleOppositeTitle("葉面積(㎡)", "出葉枚");
        this.#setAnnotationAndScale(area);
        this.graphDataSeries.addDataSeries("実績値-葉面積(㎡)", area.values);
        this.graphDataSeries.addDataSeries("推定値-葉面積(㎡)", area.predictValues);
        this.graphDataSeries.addDataSeries("実測値-葉面積(㎡)", area.meauredValues);
        this.optionsMapper.addColor("#223c7d");
        this.graphDataSeries.addDataSeries("実績値-出葉枚", count.values);
        this.optionsMapper.addColor("#14782f");
        this.graphDataSeries.addDataSeries("推定値-出葉枚", count.predictValues);
        this.optionsMapper.addColor("#91621a");
        this.graphDataSeries.addDataSeries("実測値-出葉枚", count.meauredValues);
    }
    //* ============================================
    // 光合成推定グラフ設定
    //* ============================================
    setPhotoSynthesysGraph(source) {
        this.#setTitles(source, "光合成推定モデル", "光合成量(kgCO2 vine^-1)");
        this.#setAnnotationAndScale(source);
        this.#setData(source.values, source.predictValues, source.meauredValues);
    }
}
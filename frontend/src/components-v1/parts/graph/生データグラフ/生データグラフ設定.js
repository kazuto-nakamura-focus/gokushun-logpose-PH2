import { LineGraphOptions } from "@/components-v1/parts/graph/線グラフオプション.js";
import { GraphAnnotation } from "@/components-v1/parts/graph/グラフアノテーション.js";
import { GraphDataSeries } from "@/components-v1/parts/graph/グラフデータ.js";

export class RawDataSettings {
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
    #setTitles(title, xtitle, ytitle) {
        // タイトル
        this.optionsMapper.setGraphTitle(title);
        // X軸タイトル
        this.optionsMapper.setXScaleTitle(xtitle);
        // Y軸タイトル
        this.optionsMapper.setYScaleTitle(ytitle);
    }
    //* ============================================
    // アノテーションと目盛設定
    //* ============================================
    #setAnnotationAndScale(source) {
        // Y軸の最高値
        let max = source.yend;
        // カテゴリー
        this.optionsMapper.setXCategory(source.category);
        // 目盛
        this.optionsMapper.setYScale(max);
    }
    //* ============================================
    // データ設定
    //* ============================================
    #setData(values, predictValues) {
        this.graphDataSeries.addDataSeries("実績値", values);
        this.graphDataSeries.addDataSeries("推定値", predictValues);
    }
    //* ============================================
    // 生データグラフ設定
    //* ============================================
    setGrowthGraph(title, xtitle, ytitle, source) {
        this.#setTitles(title, xtitle, ytitle);
        this.#setAnnotationAndScale(source);
        this.#setData(source.values, source.predictValues, source.meauredValues);
    }

}
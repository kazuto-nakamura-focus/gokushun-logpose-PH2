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
        this.optionsMapper.setInterval(null, 0, source.category.length, source.flags);
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
        if (values.length > 0) {
            this.optionsMapper.addColor("#2196f3");
            this.graphDataSeries.addDataSeries("実績値", values);
        }
        if (predictValues.length > 0) {
            this.optionsMapper.addColor("#00b428");
            this.graphDataSeries.addDataSeries("推定値", predictValues);
        }
        if (meauredValues.meauredValues > 0) {
            this.optionsMapper.addColor("#fb8c00");
            this.graphDataSeries.addDataSeries("実測値", meauredValues);
        }
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
        this.optionsMapper.setGraphTitle("葉面積と出葉枚数");
        this.optionsMapper.setInterval(null, 0, area.category.length, area.flags);

        // サブタイトル
        let subtitleCount = 0;
        if (null != area.estimated) {
            this.optionsMapper.addSubTitleElement("葉面積(㎡)", area.estimated);
            subtitleCount++;
        }
        if (null != count.estimated) {
            this.optionsMapper.addSubTitleElement("出葉枚数", count.estimated);
            subtitleCount++;
        }
        if (subtitleCount > 0) this.optionsMapper.createSubtitle();
        // アノテーション
        this.optionsMapper.setXAnnotations(this.annotationMapper.getTodayAnnotation());
        // カテゴリー
        this.optionsMapper.setXCategory(area.category);

        // Y軸複合タイトル
        this.optionsMapper.declareMultiGraph();

        this.optionsMapper.declareYScaleTitle("実績値-出葉枚数", count.yend, true);
        this.optionsMapper.addColor("rgba(34,83,125, 0.5)");
        this.graphDataSeries.addDataSeries("実績値-出葉枚数", count.values);

        if (count.predictValues.length > 0) {
            this.optionsMapper.addYScaleTitle("実績値-葉面積(㎡)");
            this.optionsMapper.addColor("rgba(20,120,47,0.5)");
            this.graphDataSeries.addDataSeries("推定値-出葉枚数", count.predictValues);
        }

        if (count.meauredValues.length > 0) {
            this.optionsMapper.addYScaleTitle("実績値-葉面積(㎡)");
            this.optionsMapper.addColor("rgba(145,98,26, 0.5)");
            this.graphDataSeries.addDataSeries("実測値-出葉枚数", count.meauredValues);
        }


        this.optionsMapper.declareYScaleTitle("実績値-葉面積(㎡)", area.yend, false);
        this.optionsMapper.addColor("#2196f3");
        this.graphDataSeries.addDataSeries("実績値-葉面積(㎡)", area.values);

        if (area.predictValues.length > 0) {
            this.optionsMapper.addYScaleTitle("実績値-葉面積(㎡)");
            this.optionsMapper.addColor("#00b428");
            this.graphDataSeries.addDataSeries("推定値-葉面積(㎡)", area.predictValues);
        }

        if (area.meauredValues.length > 0) {
            this.optionsMapper.addYScaleTitle("実績値-葉面積(㎡)");
            this.optionsMapper.addColor("#fb8c00");
            this.graphDataSeries.addDataSeries("実測値-葉面積(㎡)", area.meauredValues);
        }

    }
    //* ============================================
    // 光合成推定グラフ設定
    //* ============================================
    setPhotoSynthesysGraph(source) {
        this.#setTitles(source, "光合成推定モデル", "光合成量(kgCO2 vine^-1)");
        this.optionsMapper.setInterval(null, 0, source.category.length, source.flags);
        this.#setAnnotationAndScale(source);
        this.#setData(source.values, source.predictValues, source.meauredValues);
    }
}
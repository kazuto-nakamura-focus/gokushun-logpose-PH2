import moment from "moment";

export class GraphPanel {
    constructor() {
        this.id;
        this.selectedItems;
        this.modelId;
        this.modelName;
        this.title;
        this.subname;
        this.charOption;
        this.chartData;
        this.comment = "";
        this.annotationLabel = "";
    }
    //* ============================================
    // パネルタイトルの設定
    //* ============================================
    setPanelTitle(selectedItems) {
        this.selectedItems = selectedItems;
        this.title =
            selectedItems.selectedField.name +
            "|" +
            selectedItems.selectedDevice.name +
            "|" +
            selectedItems.selectedYear.id;
        this.subname = selectedItems.selectedModel.name;
        this.modelId = selectedItems.selectedModel.id;
        this.modelName = selectedItems.selectedModel.name;
    }
    //* ============================================
    // グラフデータの作成
    //* ============================================
    setGraphData(modelGraphSettings) {
        this.charOption = modelGraphSettings.getChartOptions();
        this.chartData = modelGraphSettings.getSeries();
    }
    //* ============================================
    // コメントの設定
    //* ============================================
    setComment(data) {
        this.comment = data.comment;
        if (null != this.comment) {
            this.comment = "コメント:" + this.comment;
        }
        //生育名毎の閾値
        const annotations = data?.annotations;

        //生育名の値を順番に比較するためのインデックス
        const tempLabels = [];
        if (annotations !== undefined && annotations !== null) {
            annotations.forEach((annotation) => {
                let date = "未達";
                if (annotation.date != null) {
                    date = new moment(annotation.date).format("YYYY-MM-DD");
                }
                tempLabels.push(annotation.name + ":" + date);
            });
            if (tempLabels.length > 0) this.annotationLabel = tempLabels.join(", ");
        }
    }
}
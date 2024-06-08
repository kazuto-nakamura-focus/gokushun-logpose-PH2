import moment from "moment";

const todayXaxisAnnotation = [{
    x: new Date().getTime(),
    borderColor: '#FF0000',
    strokeDashArray: 0,
    label: {
        borderColor: '#FF0000',
        show: true,
        text: `TODAY（${moment().format("YYYY/MM/DD")}）`,
        orientation: 'horizontal',
        style: {
            color: '#FF0000',
        },
    },
}];

export class GraphAnnotation {
    constructor() {
        this.annotations = [];
    }
    //* ============================================
    // オブジェクトリファレンス
    //* ============================================
    getValueAnnotations() { return this.annotations; }
    getTodayAnnotation() { return todayXaxisAnnotation; }
    //* ============================================
    // アノテーションを設定する
    //* ============================================
    setAnnotations(max, annotationInfoList) {
        for (const item of annotationInfoList) {
            // * 最高値がアノテーションの値の方が高い場合
            if (max < item.value) {
                max = item.value;
            }
            var annotation = {
                y: item.value,
                borderColor: item.color,
                borderWidth: 1,
                strokeDashArray: 0,
                label: {
                    borderColor: item.color,
                    position: "left",
                    textAnchor: "start",
                    text: item.name,
                    style: {
                        color: '#000000',
                    },
                }
            }
            this.annotations.push(annotation);
        }
        return max;
    }
}
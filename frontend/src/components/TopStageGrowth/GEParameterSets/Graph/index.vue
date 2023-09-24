<template>
    <div class="dailogBackground">
        <v-card elevation="2">
            <div class="dailogTitlePanel">
                <!-- 選ばれたモデルルート-->
                {{ title }}
            </div>
            <!-- 日付 -->
            <div class="dailogYearPanel">
                {{ graphFieldName }}{{ this.$store.state.selectedData.selectedYears[0].title }}年度
            </div>
            <v-divider></v-divider>
            <!-- グラフ -->
            <LineGraph ref="refParameterSetGraph" class="graph_size" :chartData="chartData" :chartOptions="chartOptions"
                :chartStyle="chartStyle" :tickHeight="300" />
        </v-card>
    </div>
</template>

<script>
import LineGraph from "@/components/parts/LineGraph";

const mockChartDataB = [1, 2, 3, 2, 0, -1, -3, -2, -1, 0, 7, 6]
const mockChartDataA = [2, 3, 4, 2, 0, -2, -4, -1, -1.6, 0.8, 7.9, 2.7]

const chartDataOption = {
    options: {
        scales: {
            xAxes: [
                {
                    type: "time",
                    position: "bottom",
                    time: {
                        displayFormats: { day: "YYYY/MM" },
                        tooltipFormat: "DD/MM/YY",
                        unit: "month",
                    },
                    scaleLabel: {
                        // 軸ラベル
                        display: true, // 表示の有無
                        labelString: "年度", // ラベル
                        fontColor: "blue", // 文字の色
                        fontFamily: "sans-serif",
                        fontSize: 16, // フォントサイズ
                    },
                    ticks: {
                        callback: function (label) {
                            return label.split(":")[0]; //":"で分割した1つ目を"開催年"として抽出
                        }
                    }
                },
            ],
            yAxes: [
                {
                    ticks: {
                        min: -10,
                        max: 10,
                        stepSize: 1
                    },
                    // ticks: {
                    //   beginAtZero: true,
                    //   stepSize: 1,
                    //   max: 10,
                    // },
                    scaleLabel: {
                        // 軸ラベル
                        display: true, // 表示の有無
                        labelString: "パラメータセット", // ラベル
                        fontColor: "blue", // 文字の色
                        fontFamily: "sans-serif",
                        fontSize: 16, // フォントサイズ
                    },
                },
            ],
        },
    },
}
const chartDataDefault = {
    labels: [],
    //データセット
    datasets: [
        {
            label: "変更前",
            data: [],
            borderColor: "#C80046",
            backgroundColor: "#ffffff",
            fill: false,
            type: "line",
            lineTension: 0.3,
        },
        {
            label: "変更後",
            data: [],
            borderColor: "#5A00A0",
            backgroundColor: "#ffffff",
            fill: false,
            type: "line",
            lineTension: 0.3,
        },
    ]
}

export default {

    name: "ParameterSetGraph",
    props: {
        // shared /** MountController */: { required: true },
        fieldName: String,
        // fieldId: Number,
        year: Number,
        selectParameterSetName: String
    },
    components: {
        LineGraph,
    },
    data() {
        return {
            graphFieldId: this.$store.state.selectedData.selectedFields[0].id,
            graphFieldName: this.$store.state.selectedData.selectedFields[0].name,
            graphYears: this.$store.state.selectedData.selectedYears[0].title,
            title: this.selectParameterSetName,

            beforeGraphData: null,
            afterGraphData: null,

            chartData: chartDataDefault,
            chartOptions: chartDataOption["options"],
            chartStyle: {
                height: "200px",
            },

            // date: this.fieldId
        }
    },
    mounted() {
    },
    created() {
        this.getGrowthParamSetGraph(this.beforeGraphData, this.afterGraphData)
    },
    updated() {
    },
    methods: {
        initialize() {
            this.makeLabels(this.$store.state.selectedData.selectedYears[0].title)
            this.$refs.refParameterSetGraph.setData(this.chartData)
            // this.getGrowthParamSetGraph(this.beforeGraphData, this.afterGraphData)
        },
        getName(data) {
            console.log(data)
            this.title = data
        },
        getGrowthParamSetGraph(b, a) {
            this.beforeGraphData = b
            this.afterGraphData = a

            //labelデータ生成
            this.makeLabels(this.$store.state.selectedData.selectedYears[0].title)

            this.chartData.datasets[0].data = mockChartDataB
            this.chartData.datasets[1].data = mockChartDataA

            // 変更前
            // パラメータによる生育推定モデルグラフデータ取得（未使用）
            // useGrowthGraphByValue(this.$store.state.selectedData.selectedFields[0].id, this.$store.state.selectedData.selectedYears[0].title, b.bd, b.be, b.ad, b.ae)
            //     .then((response) => {
            //         const results = response["data"];
            //         console.log("useGrowthParamSetRemove", results);
            //         this.chartData.datasets[0].data = response["data"].data
            //         this.chartData.datasets[0].data = mockChartDataB

            //     }).catch((error) => {
            //         console.log(error)
            //     })

            //変更後
            // パラメータによる生育推定モデルグラフデータ取得（未使用）
            // useGrowthGraphByValue(this.$store.state.selectedData.selectedFields[0].id, this.$store.state.selectedData.selectedYears[0].title, a.bd, a.be, a.ad, a.ae)
            //     .then((response) => {
            //         const results = response["data"];
            //         console.log("useGrowthParamSetRemove", results);
            //         this.chartData.datasets[1].data = response["data"].data
            //         this.chartData.datasets[1].data = mockChartDataA
            //     }).catch((error) => {
            //         console.log(error)
            //     })
        },
        makeLabels(year) {
            let labels = []
            for (let i = 1; i < 13; i++) {
                if (i < 10) {
                    labels.push(year + "-0" + [i])
                } else {
                    labels.push(year + "-" + [i])
                }
            }
            console.log(labels)
            this.chartData.labels = labels
        },
    },
    watch: {

    }
}
</script>

<style scoped>
.graph_size {
    padding-bottom: 50%;
}

.dailogBackground {
    padding: 10px;
    text-align: center;
}

.dailogYearPanel {
    padding: 10px;
    text-align: center;
}
</style>
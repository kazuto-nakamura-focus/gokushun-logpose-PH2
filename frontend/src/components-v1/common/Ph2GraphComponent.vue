<template>
  <v-card elevation-6>
    <v-container>
      <v-row style="height: 20px">
        <v-col align="right">
          <div>
            <v-icon @click="handleClose()">mdi-close</v-icon>
          </div>
        </v-col>
      </v-row>
      <v-row style="height: 36px">
        <v-col align="left" style="display: flex">
          <div
            v-if="annotationLabel"
            style="
            text-align: left;
              margin-left: 3px;
              padding: 5px;
              font-size: 10pt; font-family:Yu Gothic"
          >
            <b>{{ annotationLabel }}</b>
          </div>
        </v-col>
      </v-row>
      <v-row style="padding-bottom: 20px">
        <v-col align="left" style="font-size: 10pt; font-family: Yu Gothic;margin-left:6px">
          <b>{{ comment }}</b>
        </v-col>
      </v-row>
    </v-container>
    <!-- グラフ表示 -->
    <ph-2-graphic-tool v-if="chartDisplay == true" :options="chart.options" :series="chart.series"></ph-2-graphic-tool>
  </v-card>
</template>
    
<script>
import Ph2GraphicTool from "@/components-v1/common/Ph2GraphicTool.vue";
import "@mdi/font/css/materialdesignicons.css";
import moment from "moment";

export default {
  props: {
    target: {
      type: Object, // 型がObjectである
      required: true, // 必須のプロパティである
    },
  },
  components: {
    ph2GraphicTool: Ph2GraphicTool,
  },

  data() {
    return {
      arguments: this.target,
      titlePath: "",
      time: "",
      comment: "",
      chart: { options: {}, series: [] },
      estimationLabels: [
        "萌芽推定日",
        "開花推定日",
        "ベレーゾン推定日",
        "収穫推定日",
      ],
      chartDisplay: false,
      annotationLabel: null,
    };
  },
  //* ============================================
  // グラフの表示を行う下位関数をコールする
  //* ============================================
  mounted() {
    this.titlePath = this.arguments.title;
    this.time = moment().format("YYYY-MM-DD h:mm:ss");
    if (this.arguments.isMultiple) {
      this.initialize();
    } else {
      this.initializeSingle();
    }
  },
  methods: {
    //* ============================================
    // 推定・実績グラフを作成する
    //* ============================================
    initialize() {
      //  const realGraph = this.prepareChart("実績");
      //  const measuredGraph = this.prepareChart("実測値");
      //   const predictGraph = this.prepareChart("推定");

      this.comment = this.arguments.data.comment;
      if (null != this.comment) {
        this.comment = "コメント:" + this.comment;
      }

      //「実績」値の一覧
      const values = this.arguments.data?.values;

      //「推定」値の一覧
      const predictValues = this.arguments.data?.predictValues;

      // 「実測値」値の一覧
      const meauredValues = this.arguments.data?.meauredValues;

      //生育名毎の閾値
      const annotations = this.arguments.data?.annotations;
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

      /* this.$refs.chart.updateSeries(
        [
          {
            data: realGraph.data,
          },
          {
            data: predictGraph.data,
          },
          {
            data: measuredGraph.data,
          },
        ],
        false,
        true
      );*/
      this.chart.options = this.arguments.options;
      this.chart.series = [
        {
          name: "実績値",
          data: values,
        },
        {
          name: "推定値",
          data: predictValues,
        },
        {
          name: "実測値",
          data: meauredValues,
        },
      ];

      this.chartDisplay = true;
    },
    //* ============================================
    // 単一グラフを作成する
    //* ============================================
    initializeSingle() {
      /* const graph = this.prepareChart(this.arguments.name);
      for (const item of this.arguments.data) {
        graph.data.push(item);
      }
        this.$refs.chart.updateSeries(
        [
          {
            data: graph.data,
          },
        ],
        false,
        true
      );*/
      this.chart.options = this.arguments.options;
      this.chart.series = [
        { name: this.arguments.name, data: this.arguments.data },
      ];
      this.chartDisplay = true;
    },
    //* ============================================
    // 閉じる実行
    //* ============================================
    handleClose: function () {
      this.$emit("delete", this.arguments.id);
    },
  },
};
</script>
  
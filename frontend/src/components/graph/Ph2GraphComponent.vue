<template>
  <v-card elevation-6>
    <v-container>
      <v-row>
        <v-col align="left">
          <div
            style="
              padding: 5px;
              display: flex;
              border: 1px solid rgb(204, 204, 204);
              border-radius: 5px;
              box-shadow: rgb(185, 184, 184) 5px 5px 5px;
              width: fit-content;
            "
          >
            <div style="font-size: 10pt;">
              <b>{{ titlePath }}</b>
            </div>
            <div style="font-size: 10pt">
              &nbsp;<small
                ><i>{{ time }}</i></small
              >
            </div>
          </div>
        </v-col>
        <v-col align="right">
          <div @click="handleClose()">
            <v-icon>mdi-close</v-icon>
          </div>
        </v-col>
      </v-row>
    </v-container>
    <!-- グラフ表示 -->
    <apexchart
      type="area"
      width="100%"
      height="400"
      ref="chart"
      :options="chart.options"
      :series="chart.series"
    ></apexchart>
  </v-card>
</template>
    
<script>
import VueApexCharts from "vue-apexcharts";
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
    apexchart: VueApexCharts,
  },

  data() {
    return {
      arguments: this.$props.target,
      titlePath: "",
      time: "",
      chart: { options: {}, series: [] },
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
    // グラフデータのスペースを準備する
    //* ============================================
    prepareChart(name) {
      const item = {
        name: name,
        data: [],
      };
      this.chart.series.push(item);
      return item;
    },
    //* ============================================
    // 推定・実績グラフを作成する
    //* ============================================
    initialize() {
      const realGraph = this.prepareChart("実績");
      const predictGraph = this.prepareChart("推定");

      for (const item of this.arguments.data.values) {
        predictGraph.data.push(null);
        realGraph.data.push(item);
      }

      for (const item of this.arguments.data.predictValues) {
        realGraph.data.push(null);
        predictGraph.data.push(item);
      }
      this.$refs.chart.updateSeries(
        [
          {
            data: realGraph.data,
          },
          {
            data: predictGraph.data,
          },
        ],
        false,
        true
      );
      this.chart.options = this.arguments.options;
      this.$refs.chart.updateOptions(this.chart.options, false, true, false);
      this.$refs.chart.refresh();
    },
    //* ============================================
    // 単一グラフを作成する
    //* ============================================
    initializeSingle() {
      const graph = this.prepareChart(this.arguments.name);
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
      );
      this.chart.options = this.arguments.options;
      this.$refs.chart.updateOptions(this.chart.options, false, true, false);
      this.$refs.chart.refresh();
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
  
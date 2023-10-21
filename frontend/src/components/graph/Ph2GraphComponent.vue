<template>
  <v-card>
    <!-- 表示ボタン -->
    <div>
      <div @click="handleClose()">
        <v-card class="ma-1" elevation-3>
          <!-- グラフに属するデバイスの階層表示 -->
          {{ titlePath }}
          <!-- 閉じるボタン -->
          <v-icon>mdi-close</v-icon>
        </v-card>
      </div>
      <!-- グラフ表示 -->
      <apexchart
        type="area"
        width="100%"
        height="400"
        ref="chart"
        :options="chart.options"
        :series="chart.series"
      ></apexchart>
    </div>
  </v-card>
</template>
    
<script>
import VueApexCharts from "vue-apexcharts";

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
      chart: { options: {}, series: [] },
    };
  },
  //* ============================================
  // グラフの表示を行う下位関数をコールする
  //* ============================================
  mounted() {
    this.$emit("run");
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
      this.titlePath = this.arguments.title;
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
      this.titlePath = this.arguments.titlePath;
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
  },
};
</script>
  
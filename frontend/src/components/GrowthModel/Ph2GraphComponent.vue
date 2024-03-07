<template>
  <v-card>
    <!-- 表示ボタン -->
    <div>
      <div @click="handleClose()">
        <v-btn class="ma-1" color="gray" depressed>
          <!-- グラフに属するデバイスの階層表示 -->
          {{ titlePath }}
          <!-- 閉じるボタン -->
          <v-icon>mdi-close</v-icon>
        </v-btn>
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
  props: { shared /** MountController */: { required: true } },
  components: {
    apexchart: VueApexCharts,
  },
  data() {
    return {
      //* 選択されたデータ
      titlePath: "",
      //* このコンポーネント自体
      node: null,
      //* チャートデータ
      chart: {
        options: {},
        series: [
          {
            name: "",
            data: [], // Y軸の値
          },
          {
            name: "",
            data: [], // Y軸の値
          },
        ],
      },
    };
  },
  mounted() {
    //* マウントされたことを通知
    this.shared.mount(this.shared);
  },
  methods: {
    initialize(titlePath, options, data) {
      //  {{ selectedItems.field }} &gt; {{ selectedItems.device }} &gt; {{ selectedItems.year }} (x)
      console.log("cp");
      this.titlePath = titlePath;
      this.chart.series[0].name = "実績";
      this.chart.series[0].data.length = 0;
      this.chart.series[1].name = "推定";
      this.chart.series[1].data.length = 0;
      for (const item of data.values) {
        this.chart.series[0].data.push(item);
        this.chart.series[1].data.push(item);
      }

      for (const item of data.predictValues) {
        this.chart.series[0].data.push(null);
        this.chart.series[1].data.push(item);
      }
      this.$refs.chart.updateSeries(
        [
          {
            data: this.chart.series[0].data,
          },
          {
            data: this.chart.series[1].data,
          },
        ],
        false,
        true
      );
      this.chart.options = options;
      this.$refs.chart.updateOptions(this.chart.options, false, true, false);
      this.$refs.chart.refresh();
    },

    initializeSingle(titlePath, options, data) {
      this.titlePath = titlePath;
      this.chart.series[0].data.length = 0;
      for (const item of data) {
        this.chart.series[0].data.push(item);
      }
      this.$refs.chart.updateSeries(
        [
          {
            data: this.chart.series[0].data,
          },
        ],
        false,
        true
      );
      this.chart.options = options;
      this.$refs.chart.updateOptions(this.chart.options, false, true, false);
      this.$refs.chart.refresh();
    },
    handleClose() {
      this.shared.onConclude(this);
    },
  },
};
</script>
  
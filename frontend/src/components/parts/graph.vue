<template>
  <div>
    <button type="button" class="close" aria-label="Close" @click="hide()">
      <span aria-hidden="true">(×)</span>
      {{ title }}
      <small v-if="subTitle2 != null">{{ subTitle2 }}&nbsp;</small>
    </button>
    <Chart
      ref="ch"
      :chartData="chartItems"
      :options="chartOptions"
      v-show="isEnabled"
      style="width: 400px"
    />
  </div>
  <!-- <v-expansion-panels v-model="panel">
    <v-expansion-panel v-for="(item, i) in 5" :key="i">
      <v-expansion-panel-header>
        {{ item[i].title }}
      </v-expansion-panel-header>
      <v-expansion-panel-content>

      </v-expansion-panel-content>
    </v-expansion-panel>
  </v-expansion-panels>-->
</template>
  
  <script>
import Chart from "./renderChart.vue";
import chartData from "@/assets/test/chartdata.json";

export default {
  props: { shared /** MountController */: { required: true } },

  data() {
    return {
      panel: [0],
      item: [],
      title: "",
      subTitle1: "",
      subTitle2: "",

      chartData: chartData,
      isEnabled: true,

      chartItems: {
        labels: [
          "12月",
          "1月",
          "2月",
          "3月",
          "4月",
          "5月",
          "6月",
          "7月",
          "8月",
          "9月",
          "10月",
          "11月",
        ],
        datasets: [
          {
            label: "月ごとの点数",
            data: [95, 70, 80, 65, 69, 80, 100, 100, 72, 81, 45, 70],
            backgroundColor: "lightblue",
          },
        ],
      },
      chartOptions: {
        maintainAspectRatio: false,
        scales: {
          xAxes: [
            {
              display: true,
              // X軸グリッド非表示
              gridLines: {
                display: false,
              },
            },
          ],
          // Y軸
          yAxes: [
            {
              display: true,
              position: "right",
              ticks: {
                // 0から始まる
                beginAtZero: true,
                // 最大5目盛
                maxTicksLimit: 5,
              },
            },
          ],
        },
      },
    };
  },
  components: {
    Chart,
  },
  mounted() {
    this.shared.mount(this);
  },
  methods: {
    initialize(title, subTitle, selectedData) {
      //*** ここでAPIを呼び出し
      this.title = title;
      this.subTitle1 = this.subTitle2 = subTitle;
      // 表示設定
      this.chartItems.labels = this.chartData.data.labels;
      this.chartOptions = this.chartData.options;
      // タイトル
      var item = { title: title };
      this.item = [];
      this.item.push(item);
      // データの設定
      this.chartItems.datasets = selectedData;
      this.$refs.ch.fullRerender(this.chartItems, this.chartOptions);
    },
    hide() {
      this.isEnabled = !this.isEnabled;
      if (this.isEnabled) {
        this.$refs.ch.fullRerender(this.chartItems, this.chartOptions);
        this.subTitle2 = this.subTitle1;
      } else {
        this.subTitle2 = null;
      }
    },
  },
};
</script>

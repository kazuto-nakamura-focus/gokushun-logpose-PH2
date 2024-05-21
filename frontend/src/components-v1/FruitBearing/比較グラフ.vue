<template>
  <v-card>
    <div style="margin: 10px; color: black">
      <b>{{ title }}</b>
    </div>
    <apexchart
      type="bar"
      width="90%"
      :options="options"
      :series="series"
      ref="refChart"
    ></apexchart>
  </v-card>
</template>
<script>
import VueApexCharts from "vue-apexcharts";

export default {
  data() {
    return {
      title: null,
      options: {
        chart: {
          type: "bar",
        },
        plotOptions: {
          bar: {
            horizontal: true,
            barhegiht: "30px",
          },
        },
        yaxis: {
          labels: {
            trim: false,
            style: {
              fontSize: "12px",
              whiteSpace: "nowrap", // 改行せずに表示
              overflow: "visible", // オーバーフローを表示
            },
          },
        },
        xaxis: {
          labels: {
            trim: false,
            style: {
              fontSize: "12px",
              whiteSpace: "pre-wrap", // 改行を保持する
            },
          },
          categories: [],
        },
        annotations: {
          xaxis: [
            {
              borderColor: "#00E396",
              borderWidth: 3,
            },
          ],
        },
      },
      series: [
        {
          data: [],
        },
      ],
    };
  },
  components: { apexchart: VueApexCharts },
  methods: {
    initialize(title, name, baseVaue, datalist) {
      console.log("aaa");
      this.options.xaxis.categories.length = 0;
      this.series[0].data.length = 0;

      this.options.annotations.xaxis[0].x = baseVaue;
      this.title = title;
      for (const item of datalist) {
        this.options.xaxis.categories.push(
          item.name + " \n[" + item.year + "]"
        );
        this.series[0].data.push(item[name]);
      }
      this.$refs.refChart.updateOptions(this.options);
      this.$refs.refChart.updateSeries(this.series);
      this.$refs.refChart.refresh();
    },
  },
};
</script>
<style lang="css">
</style>
<template>
  <v-card width="100%">
    <v-container>
      <v-row>
        <div style="margin: 10px; color: black">
          <b>{{ title }}</b>
        </div>
      </v-row>
      <v-row>
        <div style="margin: 10px; color: black">
          <Small
            >表示文字列が切れてしまう場合は、画面サイズを変更してみてください。</Small
          >
        </div>
      </v-row>
      <v-row>
        <div style="width: 100%; display: block; margin-left: 60px">
          <apexchart
            type="bar"
            width="90%"
            :height="heightCalc()"
            :options="options"
            :series="series"
            ref="refChart"
          ></apexchart>
        </div>
      </v-row>
    </v-container>
  </v-card>
</template>
<script>
import VueApexCharts from "vue-apexcharts";

export default {
  data() {
    return {
      title: null,
      isCharts: false,
      options: {
        chart: {
          type: "bar",
        },
        plotOptions: {
          bar: {
            horizontal: true,
            columnWidth: "90%",
            barHeight: "30%",
          },
        },
        yaxis: {
          labels: {
            fontSize: "12px",
          },
        },
        xaxis: {
          labels: {
            trim: false,
          },
          categories: [""],
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
        let parts = item.name.split("|");
        this.options.xaxis.categories.push([parts[0], parts[1], item.year]);
        this.series[0].data.push(item[name]);
      }
      this.$nextTick(function () {
        this.$refs.refChart.updateOptions(this.options);
        this.$refs.refChart.updateSeries(this.series);
        this.$refs.refChart.refresh();
      });
    },
    heightCalc() {
      return "" + (this.series[0].data.length * 80 + 200) + "px";
    },
  },
};
</script>
<style lang="css" scoped>
</style>
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
            v-show="isCharts"
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
            maxWidth: 250,
            textAlign: "left",
            fontWeight: 550,
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
      this.options.xaxis.categories.length = 0;
      this.series[0].data.length = 0;

      this.options.annotations.xaxis[0].x = baseVaue;
      this.title = title;
      for (const item of datalist) {
        let parts = this.createName(item.name);
        parts.push("[" + item.year + "]");

        this.options.xaxis.categories.push(parts);
        this.series[0].data.push(item[name]);
      }
      this.isCharts = true;
      this.$nextTick(function () {
        //  this.$refs.refChart.updateOptions(this.options);
        //  this.$refs.refChart.updateSeries(this.series);
        this.$refs.refChart.refresh();
      });
    },
    createName(name) {
      let list = [];
      // * タイプで分ける
      let types = name.split("|");
      /*   const regex = /.{1,8}/g;
      for (const type of types) {
        let parts = type.match(regex);
        list.push(...parts);
      }*/
      list.push(...types);
      return list;
    },
    heightCalc() {
      return "" + (this.series[0].data.length * 80 + 200) + "px";
    },
  },
};
</script>
<style lang="css" scoped>
</style>
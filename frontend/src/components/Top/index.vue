<template>
  <!--トップ画面 -->
  <!-- ID001 -->
  <v-app>
    <v-container>
      <v-row>
        <v-col cols="12">
          <template>
            <v-container>
              <v-row justify="center">
                <carousel
                  :navigationEnabled="true"
                  :perPageCustom="[
                    [0, 1],
                    [720, 2],
                    [1325, 3],
                  ]"
                >
                  <slide v-for="item in displayData" :key="item.deviceId">
                    <div class="slider-inner" v-if="item.visible">
                      <div class="data-contents">
                        <div class="table_header pt-5 pb-5 pl-3 pr-3">
                          <div class="table_header_title">
                            {{ item.title }}
                          </div>
                          <div class="table_header_comment">
                            {{ item.comment }}
                          </div>
                        </div>
                        <div class="flexbox">
                          <div
                            v-for="komoku in item.items"
                            :key="komoku.idx"
                            class="box-item"
                          >
                            <div v-if="komoku.variable == ''">
                              {{ komoku.name }}
                              <br />
                              {{ komoku.value }}
                            </div>

                            <a
                              v-if="komoku.variable != ''"
                              @click="openModel(komoku)"
                            >
                              {{ komoku.name }}
                              <br />
                              {{ komoku.value }}
                              <small>{{ komoku.unit }}</small>
                              <br />
                              <i
                                ><small>{{ komoku.date }}</small></i
                              >
                            </a>
                          </div>
                        </div>
                      </div>
                    </div>
                  </slide>
                </carousel>
              </v-row>
            </v-container>
          </template>
        </v-col>
      </v-row>
      <div style="width: 100%; text-align: center">
        <div style="width: 100%; text-align: center">
          <SpecificDataAggregation
            v-show="isAggregated"
            ref="sa"
            :shared="sharedAggregation"
          />
        </div>
      </div>
    </v-container>
    <wait-dialog ref="wait" />
  </v-app>
</template>

<script>
import displayOrder from "@/assets/displayOrder.json";
import totalData from "@/assets/test/totalData.json";
import { TopDataParser } from "@/lib/topDataParser.js";
import { MountController } from "@/lib/mountController.js";
//参照 https://ssense.github.io/vue-carousel/examples/
//参照 https://github.com/SSENSE/vue-carousel#development
import { Carousel, Slide } from "vue-carousel";
import { useFields } from "@/api/Top";
import WaitDialog from "@/components/dialog/WaitDialog.vue";

// import unselected from "@/components/parts/menu.vue";
import SpecificDataAggregation from "./SpecificDataAggregation.vue";

export default {
  data() {
    return {
      totalData: totalData,
      selectedModel: null,
      displayOrder: displayOrder,
      TopDataParser: new TopDataParser(),
      sourceData: [],
      displayData: [],

      sharedAggregation: new MountController(),
      isAggregated: false,
    };
  },

  components: {
    Carousel,
    Slide,
    SpecificDataAggregation,
    WaitDialog,

    // unselected,
  },

  mounted: function () {
    this.$refs.wait.start("データ取得中です。しばらくお待ちください。", true);
    //console.log(this.displayOrder);
    useFields()
      .then((response) => {
        //成功時
        const results = response["data"];
        this.sourceData = this.TopDataParser.parse(results.data);
        //console.log("created", this.sourceData);
        for (const item of this.sourceData) {
          console.log(item);
          item.visible = new Object();
          item.visible = true;
          this.displayData.push(item);
        }
        //console.log(this.sourceData);
        this.$store.dispatch("changeSourceData", this.sourceData);
        this.$refs.wait.finish();
      })
      .catch((error) => {
        //失敗時
        console.log(error);
        this.$refs.wait.finish();
      });
  },

  methods: {
    openModel: function (komoku) {
      if (this.isAggregated) {
        this.$refs.sa.initialize(komoku);
      }
      this.sharedAggregation.setUp(
        this.$refs.sa,
        function (sa) {
          sa.initialize(komoku);
          this.isAggregated = true;
        }.bind(this),
        function () {}.bind(this)
      );
    },

    hide: function (item) {
      var backup = this.displayData;
      this.displayData = [];
      item.visible = false;
      for (const elem of backup) {
        if (elem === item) continue;
        this.displayData.push(elem);
      }
      this.$refs.mn.items.push(item);
    },

    recover: function (item) {
      item.visible = true;
      this.displayData.unshift(item);
    },
  },
};
</script>
<style lang="scss" scoped>
.VueCarousel {
  height: auto;
  width: 90%;
}
.VueCarousel-wrapper,
.VueCarousel-inner,
.VueCarousel-slide {
  height: 100% !important;
  // background: rgb(250, 250, 250);
  // background: linear-gradient(
  //   180deg,
  //   rgba(250, 250, 250, 1) 0%,
  //   rgb(200, 0, 70) 100%
  // );
}
.VueCarousel-slide .slider-inner {
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;

  // padding: 10px;
  //color: #fff;
  // border: 2px solid #fff;
  // font-size: 30px;
  // border-radius: 10px;
}
.active {
  display: block;
}
.passive {
  display: none;
}
.data-contents {
  width: 300px;
  text-align: center;
  box-shadow: rgba(14, 81, 3, 0.059) 0px 0px 6px 3px;
  -webkit-box-shadow: rgba(14, 81, 3, 0.059) 0px 0px 6px 3px;
  -moz-box-shadow: rgba(14, 81, 3, 0.059) 0px 0px 6px 3px;
}
// ヘッダー部
.table_header {
  color: #ffffff;
  background-color: rgb(0, 180, 40);
  // padding: 10px 5px, 10px, 5px;
  // padding-left: 5px;
  // padding-right: 5px;
  // padding-top: 10px;
  // padding-bottom: 10px;

  width: 100%;
  border: 1px solid #eee;
  box-shadow: rgba(14, 81, 3, 0.059) 0px 0px 6px 3px;
  -webkit-box-shadow: rgba(14, 81, 3, 0.059) 0px 0px 6px 3px;
  -moz-box-shadow: rgba(14, 81, 3, 0.059) 0px 0px 6px 3px;

  display: flex;
  white-space: nowrap;
  justify-content: space-between;
}

// データ部
.flexbox {
  width: 100%;
  display: flex;
  flex-wrap: wrap;
  background-color: #ffffff;
}
.box-item {
  width: 50%;
  padding-top: 8px;
  padding-bottom: 8px;
  font-size: 9pt;
  text-align: center;
  height: 60px;
  border: 1px solid #ddd;
}
.box-item a {
  display: block;
  cursor: pointer;
  width: 100%;
  height: 100%;
  padding: 0;
  margin: 0;
  color: #000000;
}
.box-item a:hover {
  box-shadow: 5px 5px 10px 0 rgba(0, 0, 0, 0.5);
}
</style>

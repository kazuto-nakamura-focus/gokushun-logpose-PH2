<template>
  <!--トップ画面 -->
  <!-- ID001 -->
  <v-app>
    <v-container>
      <v-row>
        <v-col cols="12">
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
                        <div class="table_header_title">{{ item.title }}</div>
                        <div class="table_header_comment">
                          {{ item.comment }}
                        </div>
                      </div>
                      <div class="device_info">
                        <div style="margin: 4px 0">
                          最終更新時刻:{{ item.date }}
                        </div>
                        <div v-if="item.forecast.length == 0">
                          <img src="@/assets/suspend-icon.png" width="40px" />
                        </div>
                        <div
                          class="device_info_item"
                          style="
                            text-align: center;
                            dosplay: flex;
                            justify-content: center;
                            padding-bottom: 5px;
                          "
                          v-if="item.forecast.length > 0"
                        >
                          <div>
                            <img :src="item.wheather_url" width="36px" />
                          </div>
                          <div>{{ item.wheather_text }}</div>
                          <div v-for="(item, i) in item.forecast" :key="i">
                            <div
                              style="
                                text-align: center;
                                font-size: 7pt;
                                line-height: 10pt;
                                margin-right: 6px;
                              "
                            >
                              <img :src="item.url" width="20px" />
                              <br />
                              {{ item.time }}
                              <br />
                              {{ item.text }}
                            </div>
                          </div>
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
                          </a>
                        </div>
                      </div>
                    </div>
                  </div>
                </slide>
              </carousel>
            </v-row>
          </v-container>
          <div style="width: 100%; text-align: center">
            <v-btn
              color="gray"
              class="ma-2 black--text"
              elevation="2"
              height="27px"
              @click="displaySettingDialog"
              ><span style="font-size: 9pt">表示設定</span></v-btn
            >
          </div>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="12">
          <SpecificDataAggregation
            v-show="isAggregated"
            ref="sa"
            :shared="sharedAggregation"
          />
        </v-col>
      </v-row>
    </v-container>
    <wait-dialog ref="wait" />
    <v-dialog v-model="isOrderSettingDialog" width="auto" maxHeight="400px">
      <ph-2-setting-order
        :displayData="displayData"
        :unDisplayData="unDisplayData"
        @save="setDisplayList"
        @close="closeSettingDialog"
        ref="refPh2SettingOrder"
      >
      </ph-2-setting-order>
    </v-dialog>
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
import WaitDialog from "@/components-v1/parts/dialog/WaitDialog.vue";
// import unselected from "@/components/parts/menu.vue";
import SpecificDataAggregation from "./SpecificDataAggregation.vue";
import Ph2SettingOrder from "@/components/Top/並び順設定.vue";
import { AuthCookies } from "@/lib/AuthCookies.js";

export default {
  data() {
    return {
      totalData: totalData,
      selectedModel: null,
      displayOrder: displayOrder,
      TopDataParser: new TopDataParser(),
      sourceData: [],
      displayData: [],
      unDisplayData: [],

      sharedAggregation: new MountController(),
      isAggregated: false,

      isOrderSettingDialog: false,
    };
  },

  components: {
    Carousel,
    Slide,
    SpecificDataAggregation,
    WaitDialog,
    Ph2SettingOrder,
  },

  mounted: function () {
    this.$refs.wait.start("データ取得中です。しばらくお待ちください。", true);
    this.setOAuth();
    //console.log(this.displayOrder);
    useFields()
      .then((response) => {
        const results = response["data"];
        this.sourceData = this.TopDataParser.parse(results.data);
        this.setDisplayList();
        var koumoku = {
          variable: 1,
          name: "温度",
        };
        this.openModel(koumoku);
      })
      .catch((error) => {
        console.log(error);
      })
      .finally(() => {
        this.$refs.wait.finish();
      });
  },

  methods: {
    setOAuth: function () {
      let cookies = new AuthCookies();
      // 引数に設定があればそれを設定する
      if (this.$route.query.id !== undefined) {
        cookies.set("id", this.$route.query.id, 90);
      }
      if (this.$route.query.name !== undefined) {
        cookies.set("name", this.$route.query.name, 90);
      }
      if (this.$route.query.at !== undefined) {
        cookies.set("at", this.$route.query.at, 90);
      }
      console.log(this.$route.query.at);
      console.log(cookies.get("at"));
    },
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
    //* ============================================
    // 並び順設定画面を表示する
    //* ============================================
    displaySettingDialog() {
      this.isOrderSettingDialog = true;
    },
    //* ============================================
    // 並び順設定画面を閉じる
    //* ============================================
    closeSettingDialog() {
      this.isOrderSettingDialog = false;
    },
    //* ============================================
    // 表示順を決めながらリストを設定する
    //* ============================================
    setDisplayList() {
      this.isOrderSettingDialog = false;
      let dataList = this.sourceData;
      this.displayData.length = 0;
      this.unDisplayData.length = 0;
      for (const srcItem of dataList) {
        srcItem.visible = new Object();
        srcItem.visible = false;
      }
      // * ローカルから順序情報を得る
      let savedList = JSON.parse(localStorage.getItem("showList"));
      if (savedList !== undefined && null != savedList) {
        for (const savedItem of savedList) {
          for (const srcItem of dataList) {
            if (savedItem.deviceId == srcItem.deviceId) {
              srcItem.visible = true;
              this.displayData.push(srcItem);
              break;
            }
          }
        }
        for (const srcItem of dataList) {
          if (srcItem.visible != true) {
            this.unDisplayData.push(srcItem);
          }
        }
      } else {
        for (const srcItem of dataList) {
          srcItem.visible = true;
          this.displayData.push(srcItem);
        }
      }
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
.device_info {
  background-color: #f5fbe5;
  font-size: 9pt;
  text-align: center;
}
.device_info_item {
  background-color: #f5fbe5;
  font-size: 9pt;
  text-align: left;
  width: 100%;
  display: flex;
  flex-wrap: wrap;
  line-height: 30px;
  margin-right: 6px;
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
  padding-top: 6px;
  padding-bottom: 6px;
  font-size: 9pt;
  text-align: center;
  height: 50px;
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
@/lib/AuthCookies.js
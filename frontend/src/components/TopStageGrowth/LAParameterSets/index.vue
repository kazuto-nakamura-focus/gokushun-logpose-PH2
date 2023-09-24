<!--パラメーターセット表示-->
<!--パラメーターセット編集画面-->
<template>
  <v-app>
    <v-container>
      <v-dialog v-model="isDialog" width="800">
        <v-card>
          <v-card-title v-if="title != null" class="text-h5">{{
            title
          }}</v-card-title>
          <v-container>
            <toggle-button
              :value="false"
              :checked="isEditMode"
              v-model="isEditMode"
              :width="125"
              :height="27"
              :labels="{ checked: '編集モード', unchecked: '表示モード' }"
            ></toggle-button>
            <v-tabs v-model="tabModel">
              <v-tab href="#tab-1">パラメータセット名</v-tab>
              <v-tab href="#tab-2">履歴</v-tab>
              <v-tab-item value="tab-1">
                <v-card-text>
                  <div class="modal-tab">
                    <ParameterSetName
                      @deleteParameterSet="deleteParameterSet"
                      @close="close"
                      @overwriteSave="overwriteSave"
                      @aliasSave="aliasSave"
                      @getItems="getItems"
                      ref="refParameterSetName"
                      :shared="sharedInput"
                      :isEditMode="isEditMode"
                      :leafAreaData="leafAreaData"
                      :parameterSetList="parameterSetList"
                    />
                  </div>
                </v-card-text>
              </v-tab-item>
              <v-tab-item value="tab-2">
                <div style="width: 100%">
                  <history ref="refHistory" :shared="sharedHistory"></history>
                </div>
              </v-tab-item>
            </v-tabs>
          </v-container>
        </v-card>
      </v-dialog>
    </v-container>
  </v-app>
</template>

<script>
import { MountController } from "@/lib/mountController.js";
import { mdiExitToApp } from "@mdi/js";
import ParameterSetName from "./ParameterSetName/index.vue";
import history from "@/components/TopStageGrowth/ParameterSet/history.vue";
import moment from "moment";

// import LAParameterSetsData from "@/assets/testData/LAParameterSets.json"
import {
  useLeafParamSetList,
  useLeafParamDefaultId,
  useLeafParamSetDetail,
  useLeafParamSetUpdate,
  useLeafParamSetAdd,
  useLeafParamSetRemove,
} from "@/api/TopStateGrowth/LAParameterSets";

export default {
  props: {
    shared /** MountController */: { required: true },
    title: {
      type: String,
      required: false,
    },
  },

  data() {
    return {
      isDialog: false,
      sharedInput: new MountController(),
      sharedHistory: new MountController(),
      modelItem: null,
      sharedGraph: new MountController(),
      selectedInfo: null,
      tabModel: "tab-1",
      savedModels: [],
      savedModel: null,
      path: mdiExitToApp,

      // defaultLeafAreaData: LAParameterSetsData,
      leafAreaData: {},
      isEditMode: false,
      parameterSetList: [],
      saveParameter: [],
      saveHistory: [],
    };
  },

  components: {
    ParameterSetName,
    history,
  },

  created() {
    this.getItems(null);
  },
  mounted() {
    this.shared.mount(this);
  },
  updated() {
    // console.log(this.sproutData);
    // console.log(this.isEditMode);
    // console.log(this.saveHistory)
  },

  methods: {
    initialize: function (selectedInfo) {
      this.modelItem = selectedInfo.menu.selectedModel.id;
      this.selectedInfo = selectedInfo;
      useLeafParamDefaultId(
        2,
        selectedInfo.menu.selectedDevice.id,
        selectedInfo.menu.selectedYear.id
      )
        .then((response) => {
          this.defaultId = response.data.data;
          this.getItems(this.defaultId);
        })
        .catch((error) => {
          console.log(error);
        });
    },

    getItems: function (selectedId) {
      //葉面積・葉枚数パラメータセットリスト取得
      useLeafParamSetList()
        .then((response) => {
          console.log(response);
          const paramSetList = response["data"].data;
          this.parameterSetList = paramSetList;

          //葉面積・葉枚数パラメータセット詳細取得
          useLeafParamSetDetail(selectedId)
            .then((detail_response) => {
              console.log(detail_response);
              const detail_data = detail_response["data"]["data"];

              this.leafAreaData = {
                createdDate: detail_data.date,
                userId: detail_data.userId,
                author: detail_data.name,
                content: detail_data.comment,
                paramid: detail_data.id,
                paramYear: detail_data.year,
                paramDevice: detail_data.deviceId,
                default: detail_data.default,
                leafAreaTitle: detail_data.parameterName,
                leafAreaAA: detail_data.areaA,
                leafAreaAB: detail_data.areaB,
                leafAreaAC: detail_data.areaC,
                leafAreaNC: detail_data.countC,
                leafAreaND: detail_data.countD,
              };
              // console.log("--- leafAreaData ---");
              // console.log(this.leafAreaData);

              //葉面積・葉枚数パラメータセット更新履歴取得

              this.sharedInput.setUp(
                this.$refs.refParameterSetName,
                function (comp) {
                  comp.initialize(this.parameterSetList);
                }.bind(this),
                function () {}.bind(this)
              );

              this.sharedHistory.setUp(
                this.$refs.refHistory,
                function (comp) {
                  comp.initialize(selectedId);
                }.bind(this),
                function () {}.bind(this)
              );
            })
            .catch((error) => {
              console.log(error);
            });
        })
        .catch((error) => {
          console.log(error);
        });
    },

    deleteItem: function () {
      this.isDialog = false;
      this.shared.onConclude(this.modelItem, false);
    },

    openHistory: function () {
      this.$refs.hi.open = true;
      this.$refs.hi.modelItem = this.modelItem;
    },

    save: function () {
      this.isDialog = false;
      console.log("LAParameterSet 更新データ取得");
      this.getItems(null);
      this.shared.onConclude(this.modelItem, true);
    },

    apply: function () {
      //* メニューに追加する
      var hasValue = false;
      for (const item in this.savedModels) {
        if (item == this.modelItem.expression) {
          hasValue = true;
        }
      }
      if (!hasValue) {
        this.savedModels.push(this.modelItem.expression);
      }
      this.tabModel = "tab-2";
      this.$refs.gr.initialize(
        this.selectedInfo.title,
        this.selectedInfo.fields,
        this.selectedInfo.dates,
        this.modelItem.expression
      );
    },

    recover: function () {
      this.modelItem.expression = this.savedModel;
    },

    close: function () {
      (this.isEditMode = false), (this.isDialog = false);
    },
    // historySave(data) {
    //   //履歴保存API

    //   //削除予定
    //   this.saveHistory.unshift(data)
    //   // this.tabModel = "tab-3";
    // },

    //パラメータセット
    deleteParameterSet(deleteParameterSetData) {
      console.log("deleteParameterSet {}", deleteParameterSetData);
      //葉面積・葉枚数パラメータセット削除
      useLeafParamSetRemove(deleteParameterSetData.id)
        .then((response) => {
          console.log(response);
          this.save();
        })
        .catch((error) => {
          console.log(error);
        });
    },

    overwriteSave(overwriteSaveData) {
      // 登録用データ作成
      this.saveParameter = {
        id: overwriteSaveData.id,
        name: overwriteSaveData.author,
        userId: this.$store.state.login.userId,
        comment: overwriteSaveData.content,
        parameterName: overwriteSaveData.leafAreaTitle,
        areaA: overwriteSaveData.leafAreaAA,
        areaB: overwriteSaveData.leafAreaAB,
        areaC: overwriteSaveData.leafAreaAC,
        countC: overwriteSaveData.leafAreaNC,
        countD: overwriteSaveData.leafAreaND,
      };
      this.$set(this.saveParameter, "date", moment().format("YYYY-MM-DD"));
      console.log("overwriteSave {}", this.saveParameter);

      //葉面積・葉枚数パラメータセット更新
      useLeafParamSetUpdate(this.saveParameter)
        .then((response) => {
          console.log(response);
          this.save();
        })
        .catch((error) => {
          console.log(error);
        });
    },

    aliasSave(aliasSaveData) {
      // 登録用データ作成
      this.saveParameter = {
        name: aliasSaveData.author,
        userId: this.$store.state.login.userId,
        comment: aliasSaveData.content,
        parameterName: aliasSaveData.leafAreaTitle,
        areaA: aliasSaveData.leafAreaAA,
        areaB: aliasSaveData.leafAreaAB,
        areaC: aliasSaveData.leafAreaAC,
        countC: aliasSaveData.leafAreaNC,
        countD: aliasSaveData.leafAreaND,
      };
      this.$set(this.saveParameter, "date", moment().format("YYYY-MM-DD"));
      console.log("aliasSave {}", this.saveParameter);

      //葉面積・葉枚数パラメータセット追加
      useLeafParamSetAdd(this.saveParameter)
        .then((response) => {
          console.log(response);
          this.save();
        })
        .catch((error) => {
          console.log(error);
        });
    },
  },
};
</script>

<style lang="scss">
@import "@/style/common.css";

.theme--light.v-text-field--solo > .v-input__control > .v-input__slot {
  background: #f4f5fa;
}

.modal-tab {
  background-color: white;
  padding: 5px;
  text-align: left;
}

// .v-input__control {
//   width: 56px;
//   margin-top: 4px;
// }
</style>

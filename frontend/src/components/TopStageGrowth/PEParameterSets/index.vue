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
              v-show="tabModel == 'tab-1'"
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
                      :photosynthesisData="photosynthesisData"
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

import {
  usePhotosynthesisParamSetList,
  usePhotosynthesisDefaultId,
  usePhotosynthesisParamSetDetail,
  usePhotosynthesisParamSetUpdate,
  usePhotosynthesisParamSetAdd,
  usePhotosynthesisParamSetRemove,
} from "@/api/TopStateGrowth/PEParameterSets";

// import PEParameterSetsData from "@/assets/testData/PEParameterSets.json"

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
      defaultId: null,
      modelItem: null,
      sharedGraph: new MountController(),
      selectedInfo: null,
      tabModel: "tab-1",
      savedModels: [],
      savedModel: null,
      path: mdiExitToApp,

      // defaultPhotosynthesisData: PEParameterSetsData,
      photosynthesisData: {},
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
    console.log("PEParameterSet データ取得");
    //  this.getItems(null);
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
      usePhotosynthesisDefaultId(
        3,
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
      //光合成推定パラメータセットリスト取得
      usePhotosynthesisParamSetList()
        .then((response) => {
          console.log(response);
          const paramSetList = response["data"].data;
          this.parameterSetList = paramSetList;

          //光合成推定パラメータセット詳細取得
          usePhotosynthesisParamSetDetail(selectedId)
            .then((detail_response) => {
              console.log(detail_response);
              const detail_data = detail_response["data"]["data"];

              this.photosynthesisData = {
                createdDate: detail_data.date,
                userId: detail_data.userId,
                author: detail_data.name,
                content: detail_data.comment,
                paramid: detail_data.id,
                paramYear: detail_data.year,
                paramDevice: detail_data.deviceId,
                default: detail_data.default,
                photosynthesisTitle: detail_data.parameterName,
                photosynthesisF: detail_data.fieldF,
                photosynthesisG: detail_data.fieldG,
                photosynthesisA: detail_data.weibullA,
                photosynthesisB: detail_data.weibullB,
                photosynthesisL: detail_data.weibullL,
              };

              //光合成推定パラメータセット更新履歴取得
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

    save: function () {
      this.isDialog = false;
      console.log("PEParameterSet 更新データ取得");
      this.getItems(null);
      this.shared.onConclude(this.modelItem, true);
    },

    close: function () {
      (this.isEditMode = false), (this.isDialog = false);
    },

    //パラメータセット
    deleteParameterSet(deleteParameterSetData) {
      console.log("deleteParameterSet {}", deleteParameterSetData);
      //光合成推定パラメータセット削除
      usePhotosynthesisParamSetRemove(deleteParameterSetData.id)
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
        fieldF: overwriteSaveData.photosynthesisF,
        fieldG: overwriteSaveData.photosynthesisG,
        weibullA: overwriteSaveData.photosynthesisA,
        weibullB: overwriteSaveData.photosynthesisB,
        weibullL: overwriteSaveData.photosynthesisL,
      };
      this.$set(this.saveParameter, "date", moment().format("YYYY-MM-DD"));
      console.log("overwriteSave {}", this.saveParameter);
      //光合成推定パラメータセット更新
      usePhotosynthesisParamSetUpdate(this.saveParameter)
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
        parameterName: aliasSaveData.photosynthesisTitle,
        fieldF: aliasSaveData.photosynthesisF,
        fieldG: aliasSaveData.photosynthesisG,
        weibullA: aliasSaveData.photosynthesisA,
        weibullB: aliasSaveData.photosynthesisB,
        weibullL: aliasSaveData.photosynthesisL,
      };
      this.$set(this.saveParameter, "date", moment().format("YYYY-MM-DD"));
      console.log("aliasSave {}", this.saveParameter);

      //光合成推定パラメータセット追加
      usePhotosynthesisParamSetAdd(this.saveParameter)
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

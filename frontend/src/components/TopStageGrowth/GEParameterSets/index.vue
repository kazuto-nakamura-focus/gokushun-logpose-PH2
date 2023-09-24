<!--パラメーターセット表示-->
<template>
  <v-app>
    <v-container>
      <v-dialog v-model="isDialog" width="700">
        <v-card>
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
              <v-tab href="#tab-2">適用グラフ</v-tab>
              <v-tab href="#tab-3">履歴</v-tab>
              <v-tab-item value="tab-1">
                <v-card-text>
                  <div class="modal-tab">
                    <ParameterSetName
                      ref="refParameterSet"
                      @overwriteSave="overwriteSave"
                      @aliasSave="aliasSave"
                      :isEditMode="isEditMode"
                      @selectParameter="selectParameter"
                      :shared="sharedInput"
                      @selectBeforeParameterSet="selectBeforeParameterSet"
                      @selectAfterParameterSet="selectAfterParameterSet"
                      :selectParameterSetId="selectParameterSetId"
                      @close="close"
                    />
                  </div>
                </v-card-text>
              </v-tab-item>
              <v-tab-item value="tab-2">
                <ParameterSetGraph
                  ref="refParameterSetGraph"
                  :selectParameterSetName="selectParameterSetName"
                  :fieldName="fieldName"
                  :fieldId="fieldId"
                  :year="year"
                />
              </v-tab-item>
              <v-tab-item value="tab-3">
                <history ref="refHistory" :shared="sharedHistory"></history>
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
import ParameterSetGraph from "./Graph/index.vue";
import history from "@/components/TopStageGrowth/ParameterSet/ParameterSetHistory.vue";
import {
  useGrowthParamSetList,
  useGrowthParamDefaultId,
  useGrowthParamSetDetail,
  // useGrowthGraphByValue,
  //useGrowthParamSetUpdate,
  //  useGrowthParamSetAdd,
  // useGrowthParamSetRemove,
} from "@/api/TopStateGrowth/GEParameterSets";

export default {
  props: {
    shared /** MountController */: { required: true },
    // shared: {
    //   type: MountController,
    //   required: true,
    // },
    // title: {
    //   type: String,
    //   required: false,
    // },
    // shared /** MountController */: { required: true },
    selectedMenu: Object,
    selectedField: Array,
    selectedYears: Array,
    selectedDevices: Array,
  },
  data() {
    return {
      isDialog: false,
      modelItem: null,
      sharedInput: new MountController(),
      sharedHistory: new MountController(),
      selectedInfo: null,
      tabModel: "tab-1",
      savedModels: [],
      savedModel: null,
      path: mdiExitToApp,
      parameterSetLabel: "パラメータセット名",
      prevEditParameterLabel: "編集前パラメータ",
      afterEditParameterLabel: "編集後パラメータ",
      explainLabel: "説明（必須）",
      explainByLabel: "編集者（必須）",

      isEditMode: false,
      saveParameter: [],
      testGraph: [], //削除予定

      msFieldData: null,

      titleName: "", // 選ばれたモデル種別
      fields: [], // 選ばれた圃場
      year: null,
      params: [],

      selectParameterSetData: null,
      selectParameterSetId: null,
      selectParameterSetName: null,
      GetGEParameterSetsHistory: null,

      selectBeforeParameterSetData: null,
      selectAfterParameterSetData: null,
    };
  },

  components: {
    // グラフエリア
    // GraphArea,
    // 履歴エリア
    // HIstoryArea,
    // SvgIcon,
    // HistoryInputDialog,
    ParameterSetName,
    ParameterSetGraph,
    history,
  },

  mounted() {
    this.shared.mount(this);
  },
  updated() {},

  methods: {
    initialize: function (selectedInfo) {
      //  this.fieldName = this.$store.getters.selectedField.name;
      //  this.fieldId = this.$store.getters.selectedField.id;
      //選択したデバイス名指定
      //  this.deviceName = this.$store.getters.selectedDevice.name;
      //  this.deviceId = this.$store.getters.selectedDevice.id;
      //選択した年度
      //this.year = this.$store.getters.selectedYear.id;
      this.modelItem = selectedInfo.menu.selectedModel.id;
      this.selectedInfo = selectedInfo;
      useGrowthParamDefaultId(
        1,
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
      //パラメータセットリスト取得
      useGrowthParamSetList()
        .then((response) => {
          console.log(response);
          const paramSetList = response["data"].data;
          this.parameterSetList = paramSetList;

          //パラメータセット詳細取得
          useGrowthParamSetDetail(selectedId).then((detail_response) => {
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
            this.sharedInput.setUp(
              this.$refs.refParameterSet,
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
          });
        })
        .catch((error) => {
          console.log(error);
        });
    },
    // initialize: function (selectedInfo, arg) {

    //   this.modelItem = arg;
    //   this.selectedInfo = selectedInfo;

    //   if (!this.sharedGraph.initialized) {
    //     this.sharedGraph.setUp(
    //       this.$refs.gr,
    //       function (gr) {
    //         gr.initialize(
    //           selectedInfo.title,
    //           selectedInfo.fields,
    //           selectedInfo.dates,
    //           arg.expression
    //         );
    //       }.bind(this),
    //       function () { }.bind(this)
    //     );
    //   } else {
    //     this.$refs.gr.initialize(
    //       selectedInfo.title,
    //       selectedInfo.fields,
    //       selectedInfo.dates,
    //       arg.expression
    //     );
    //   }

    // const title = data.title;
    // const fields = data.fields;
    // const params = data.params;

    // タイトル
    // this.title = title;
    // this.titleName = "生育ステージパラメータリスト";
    // 圃場リスト
    // this.fields = fields;
    // for (const item of fields) {
    //   if (item.state) this.fields.push(item.title);
    // }
    //  this.selected = this.fields[0];
    // デバイス
    // this.params = params;
    // this.param = this.params[0];
    // this.isDialog = true;

    //  if (this.tabModel == "tab-2") {
    //    this.$refs.refParameterSetGraph.initialize();
    //  }
    // },

    deleteItem: function () {
      this.isDialog = false;
      // this.shared.onConclude(this.modelItem, false);
    },

    openHistory: function () {
      this.$refs.hi.open = true;
      this.$refs.hi.modelItem = this.modelItem;
    },

    save: function () {
      this.isDialog = false;
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
      this.isDialog = false;
    },
    overwriteSave() {
      //tab2に移動
      //this.testGraph = this.saveParameter
      // this.tabModel = "tab-2";
    },
    aliasSave() {
      //tab2に移動
      // this.testGraph = this.saveParameter
      // this.tabModel = "tab-2";
    },
    //編集パラメータセット選択
    selectParameter(data) {
      this.selectParameterSetId = data.id;
      this.selectParameterSetName = data.name;
    },
    //編集パラメータセットデータ
    selectBeforeParameterSet(data) {
      this.selectBeforeParameterSetData = null;
      this.selectBeforeParameterSetData = data;
    },
    //編集パラメータセット変更データ
    selectAfterParameterSet(data) {
      this.selectAfterParameterSetData = null;
      this.selectAfterParameterSetData = data;
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

.action-button {
  border: none;
  color: black;
  padding: 3px 12px;
  text-align: center;
  text-decoration: none;
  display: inline-block;
  font-size: 16px;
  opacity: 0.7;
  background-color: white;
}
</style>

<!--パラメーターセット表示-->
<!--パラメーターセット編集-->
<template>
  <v-container>
    <div>
      <div class="text-subtitle-1">パラメータセット名</div>
      <v-select
        v-model="selected"
        :items="parameterList"
        item-text="name"
        item-value="id"
        class="select_size ml-1"
        :disabled="!isEditMode"
        return-object
      ></v-select>
      <div>
        <div class="text-subtitle-1" v-if="!isEditMode">パラメータ</div>
        <div class="text-subtitle-1" v-if="isEditMode">編集前パラメータ</div>
        <v-container class="sprout">
          <v-row>
            <v-col cols="6">
              <v-row>
                <v-subheader class="ma-0 pa-0">萌芽前</v-subheader>
                <v-subheader class="ma-0 pa-1">d</v-subheader>
                <v-text-field
                  class="ma-0 pa-1"
                  dense
                  hide-details="auto"
                  outlined
                  filled
                  readonly
                  :value="beforeParameterSetData.bd"
                ></v-text-field>
                <v-subheader class="ma-0 pa-1">e</v-subheader>
                <v-text-field
                  class="ma-0 pa-1"
                  dense
                  hide-details="auto"
                  outlined
                  filled
                  readonly
                  :value="beforeParameterSetData.be"
                ></v-text-field>
              </v-row>
            </v-col>
            <v-col cols="6">
              <v-row>
                <v-subheader class="ma-0 pa-0">萌芽後</v-subheader>
                <v-subheader class="ma-0 pa-1">d</v-subheader>
                <v-text-field
                  class="ma-0 pa-1"
                  dense
                  hide-details="auto"
                  outlined
                  filled
                  readonly
                  :value="beforeParameterSetData.ad"
                ></v-text-field>
                <v-subheader class="ma-0 pa-1">e</v-subheader>
                <v-text-field
                  class="ma-0 pa-1"
                  dense
                  hide-details="auto"
                  outlined
                  filled
                  readonly
                  :value="beforeParameterSetData.ae"
                ></v-text-field>
              </v-row>
            </v-col>
          </v-row>
        </v-container>
      </div>
      <div v-if="!isEditMode">
        <div class="text-subtitle-1">説明</div>
        <v-text-field
          class="ma-0 pa-0"
          :value="beforeParameterSetData.comment"
          single-line
          full-width
          filled
          readonly
        ></v-text-field>
        <div class="text-subtitle-1">編集者</div>
        <v-text-field
          class="ma-0 pa-0"
          :value="beforeParameterSetData.name"
          single-line
          full-width
          filled
          readonly
        ></v-text-field>
      </div>

      <div v-if="isEditMode">
        <div class="text-subtitle-1">編集後パラメータ</div>
        <v-container class="sprout">
          <v-row>
            <v-col cols="6">
              <v-row>
                <v-subheader class="ma-0 pa-0">萌芽前</v-subheader>
                <v-subheader class="ma-0 pa-1">d</v-subheader>
                <v-text-field
                  class="ma-0 pa-1"
                  dense
                  hide-details="auto"
                  outlined
                  v-model.number="afterParameterSetData.bd"
                ></v-text-field>
                <v-subheader class="ma-0 pa-1">e</v-subheader>
                <v-text-field
                  class="ma-0 pa-1"
                  dense
                  hide-details="auto"
                  outlined
                  v-model.number="afterParameterSetData.be"
                ></v-text-field>
              </v-row>
            </v-col>
            <v-col cols="6">
              <v-row>
                <v-subheader class="ma-0 pa-0">萌芽後</v-subheader>
                <v-subheader class="ma-0 pa-1">d</v-subheader>
                <v-text-field
                  class="ma-0 pa-1"
                  dense
                  hide-details="auto"
                  outlined
                  v-model.number="afterParameterSetData.ad"
                ></v-text-field>
                <v-subheader class="ma-0 pa-1">e</v-subheader>
                <v-text-field
                  class="ma-0 pa-1"
                  dense
                  hide-details="auto"
                  outlined
                  v-model.number="afterParameterSetData.ae"
                ></v-text-field>
              </v-row>
            </v-col>
          </v-row>
        </v-container>
      </div>

      <div v-if="isEditMode">
        <div class="text-subtitle-1">説明</div>
        <v-text-field
          class="ma-0 pa-0"
          v-model="beforeParameterSetData.comment"
          single-line
          full-width
          :filled="!isEditMode"
          :readonly="!isEditMode"
        ></v-text-field>
        <div class="text-subtitle-1">編集者</div>
        <v-text-field
          class="ma-0 pa-0"
          v-model="beforeParameterSetData.name"
          single-line
          full-width
          :filled="!isEditMode"
          :readonly="!isEditMode"
        ></v-text-field>
      </div>
    </div>
    <v-card-actions>
      <v-spacer></v-spacer>
      <div v-if="!isEditMode" class="GS_ButtonArea">
        <v-btn
          color="gray"
          class="ma-2 black--text"
          elevation="2"
          @click="close"
          >キャンセル</v-btn
        >
      </div>
      <div v-if="isEditMode" class="GS_ButtonArea">
        <v-btn
          :disabled="!selected.id"
          color="primary"
          aria-disabled=""
          class="ma-2 white--text"
          elevation="2"
          @click="overwriteSave"
          >上書き保存</v-btn
        >
        <v-btn
          color="primary"
          class="ma-2 white--text"
          elevation="2"
          @click="aliasSave"
          >別名保存</v-btn
        >
        <v-btn
          :disabled="!selected.id"
          color="primary"
          class="ma-2 white--text"
          elevation="2"
          @click="deleteParameterSet"
          >削除</v-btn
        >
      </div>
    </v-card-actions>
    <v-card-actions>
      <div v-if="isEditMode" class="GS_ButtonArea">
        <v-btn
          color="gray"
          class="ma-2 black--text"
          elevation="2"
          @click="close"
          >キャンセル</v-btn
        >
      </div>
    </v-card-actions>
    <ParameterSetNameDialog
      :handleSubmit="handleSubmitParameterSetName"
      :show="isShowParameterSetName"
      confirmText="保存"
      cancelText="キャンセル"
      ref="refParameterSetName"
    />
  </v-container>
</template>

<script>
import {
  useGrowthParamSetList,
  useGrowthParamSetDetail,
  useGrowthParamSetAdd,
  useGrowthParamSetUpdate,
  useGrowthParamSetRemove,
} from "@/api/TopStateGrowth/GEParameterSets/index";
import ParameterSetNameDialog from "@/components/parts/ParameterSetName";

export default {
  name: "ParameterSetName",
  props: {
    shared: Object,
    isEditMode: Boolean,
    selectParameterSetId: Number,
  },
  components: {
    ParameterSetNameDialog,
  },
  data() {
    return {
      parameterList: null,
      selected: {
        id: null,
        name: null,
      },
      beforeParameterSetData: [],
      afterParameterSetData: {
        bd: 0,
        be: 0,
        ad: 0,
        ae: 0,
      },
      isShowParameterSetName: false,
    };
  },
  mounted() {
    this.defaultSet();
    this.shared.mount(this);
  },
  created() {},
  updated() {},
  methods: {
    initialize(list) {
      this.parameterList = list;
    },
    reset() {
      this.selected = {
        id: null,
        name: null,
      };
      this.afterParameterSetData = {
        bd: 0,
        be: 0,
        ad: 0,
        ae: 0,
      };
    },
    close() {
      this.reset();
      this.$emit("close");
    },
    async defaultSet() {
      await this.getUseGrowthParamSetList();
      await this.getUseGrowthParamSetDetail();
    },
    getUseGrowthParamSetList() {
      //生育推定パラメータセットリスト取得
      useGrowthParamSetList()
        .then((response) => {
          //成功時
          const results = response["data"];
          console.log("useGrowthParamSetList", results);
          this.parameterList = results.data.list;
          //APIエラーのためmock
          // this.parameterList = ParamSetLabel //削除
        })
        .catch((error) => {
          //失敗時
          console.log(error);
        });
    },
    getUseGrowthParamSetDetail() {
      let getParamId = this.selected.id;
      if (!getParamId) {
        getParamId = 0;
      }
      //生育推定パラメータセット詳細取得
      useGrowthParamSetDetail(getParamId)
        .then((response) => {
          //成功時
          const results = response["data"];
          this.beforeParameterSetData = results.data;
        })
        .catch((error) => {
          //失敗時
          console.log(error);
        });
    },
    //パラメータセット上書き保存
    overwriteSave() {
      let overwriteData = {
        ...this.beforeParameterSetData,
        ...this.afterParameterSetData,
        date: null,
        userId: this.$store.state.login.userId,
      };
      //生育推定パラメータセット更新
      useGrowthParamSetUpdate(overwriteData)
        .then((response) => {
          //成功時
          const results = response["data"];
          console.log("useGrowthParamSetUpdate", results);
          this.reset();
          this.getUseGrowthParamSetList();
        })
        .catch((error) => {
          //失敗時
          console.log(error);
        });
      this.$emit("overwriteSave");
    },
    //パラメータセット別名保存
    aliasSave() {
      this.$refs.refParameterSetName.display();
    },
    handleSubmitParameterSetName: function (name) {
      //生育推定パラメータセット名追加API
      const aliasData = {
        ...this.beforeParameterSetData,
        ...this.afterParameterSetData,
        parameterName: name,
        id: null,
        date: null,
        // userId: 4 //this.$store.state.login.userId
        userId: this.$store.state.login.userId,
      };
      //生育推定パラメータセット追加
      useGrowthParamSetAdd(aliasData)
        .then((response) => {
          //成功時
          const results = response["data"];
          console.log("useGrowthParamSetAdd", results);
          this.reset();
          this.getUseGrowthParamSetList();
        })
        .catch((error) => {
          //失敗時
          console.log(error);
        });
      this.$emit("aliasSave");
    },
    //削除
    deleteParameterSet() {
      console.log("削除:", this.selected.id);
      //生育推定パラメータセット削除
      useGrowthParamSetRemove(this.selected.id)
        .then((response) => {
          //成功時
          const results = response["data"];
          console.log("useGrowthParamSetRemove", results);
          this.reset();
          this.getUseGrowthParamSetList();
        })
        .catch((error) => {
          //失敗時
          console.log(error);
        });
    },
  },
  watch: {
    selected: function () {
      this.$emit("selectParameter", this.selected);
      this.getUseGrowthParamSetDetail();
    },
    beforeParameterSetData: function () {
      console.log("this.beforeParameterData", this.beforeParameterSetData);
      this.$emit("selectBeforeParameterSet", this.beforeParameterSetData);
    },
    "afterParameterSetData.bd": function () {
      console.log("this.beforeParameterData", this.afterParameterSetData);
      this.$emit("selectAfterParameterSet", this.afterParameterSetData);
    },
    "afterParameterSetData.be": function () {
      console.log("this.beforeParameterData", this.afterParameterSetData);
      this.$emit("selectAfterParameterSet", this.afterParameterSetData);
    },
    "afterParameterSetData.ad": function () {
      console.log("this.beforeParameterData", this.afterParameterSetData);
      this.$emit("selectAfterParameterSet", this.afterParameterSetData);
    },
    "afterParameterSetData.ae": function () {
      console.log("this.beforeParameterData", this.afterParameterSetData);
      this.$emit("selectAfterParameterSet", this.afterParameterSetData);
    },
  },
};
</script>

<style>
.sprout .v-input__control {
  width: 50px;
  padding: 0%;
}

.sprout .v-input__slot {
  padding: 10px;
}

.select_size {
  max-width: 40%;
  font-size: 14px;
}
</style>
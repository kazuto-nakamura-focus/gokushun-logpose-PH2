<template>
  <v-container>
    <div>
      <div class="text-subtitle-1">パラメータセット名</div>
      <v-select
        v-model="paramId"
        :items="parameterSetList"
        item-text="parameterName"
        item-value="id"
        class="select_size ml-1"
        @change="getItem"
      ></v-select>
      <!--<v-subheader class="ma-0 pa-0" v-show="!isEditMode">{{
        title
      }}</v-subheader>-->
      <div>
        <geParameterSets
          ref="refParameterSets"
          v-if="modelId == 1"
          @getItem="getItem"
          @addData="addData"
          @updateData="updateData"
          :isEditMode="isEditMode"
          :beforeParameterSetData="beforeParameterSetData"
          :afterParameterSetData="afterParameterSetData"
        ></geParameterSets>
        <laParameterSets
          ref="refParameterSets"
          v-else-if="modelId == 2"
          @getItem="getItem"
          @addData="addData"
          @updateData="updateData"
          :isEditMode="isEditMode"
          :beforeParameterSetData="beforeParameterSetData"
          :afterParameterSetData="afterParameterSetData"
        ></laParameterSets>
        <peParameterSets
          ref="refParameterSets"
          v-else-if="modelId == 3"
          @getItem="getItem"
          @addData="addData"
          @updateData="updateData"
          :isEditMode="isEditMode"
          :beforeParameterSetData="beforeParameterSetData"
          :afterParameterSetData="afterParameterSetData"
        ></peParameterSets>
      </div>
      <div v-if="!isEditMode">
        <div class="text-subtitle-1">説明</div>
        <v-text-field
          class="ma-0 pa-0"
          :value="beforeParameterSetData.comment"
          single-line
          full-width
          filled
          :readonly = "!isEditMode"
        ></v-text-field>
        <div class="text-subtitle-1">編集者</div>
        <v-text-field
          class="ma-0 pa-0"
          :value="beforeParameterSetData.name"
          single-line
          full-width
          filled
          :readonly = "!isEditMode"
        ></v-text-field>
      </div>

      <div v-if="isEditMode">
        <div class="text-subtitle-1">説明</div>
        <v-text-field
          class="ma-0 pa-0"
          :rules="[ruleContents]"
          v-model.number="afterParameterSetData.comment"
          single-line
          full-width
          :filled="!isEditMode"
          :readonly="!isEditMode"
        ></v-text-field>
        <div class="text-subtitle-1">編集者</div>
        <v-text-field
          class="ma-0 pa-0"
          :rules="[ruleAuthor]"
          v-model.number="afterParameterSetData.name"
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
          color="primary"
          class="ma-2 white--text"
          elevation="2"
          @click="overwriteSave"
          :disabled="isDisableButtos"
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
          color="primary"
          class="ma-2 white--text"
          elevation="2"
          v-show="parameterSetList.length > 2"
          @click="deleteParameterSet"
          :disabled="isDisabledDeleteBtn"
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
import ParameterSetNameDialog from "@/components/parts/ParameterSetName";
import geParameterSets from "./models/GEParameterSets.vue";
import laParameterSets from "./models/LAParameterSet.vue";
import peParameterSets from "./models/PEParameterSets.vue";
import {
  useParamSetList,
  useParamSetDelete,
} from "@/api/ParameterSetAPI.js";

export default {
  props: {
    shared: Object,
    modelId: Number,
  },
  components: {
    ParameterSetNameDialog,
    geParameterSets,
    laParameterSets,
    peParameterSets,
  },
  mounted() {
    this.isEditMode = false;
    this.shared.mount(this);
  },
  data() {
    return {
      title: "",
      paramId: 0,
      parameterSetList: [],
      beforeParameterSetData: {},
      afterParameterSetData: {},
      isEditMode: false,
      isDisableButtos: true,
      // ruleParameterSetData: (value) => !!value || "パラメータデータを入力してください。",
      ruleContents: (value) => {
        this.isDisableButtos = !value;
        return !!value || "説明を入力してください。";
      },
      ruleAuthor: (value) => {
        this.isDisableButtos = !value;
        return !!value || "編集者を入力してください。";
      },

      isShowParameterSetName: false,
      isDisabledDeleteBtn: false,
    };
  },
  methods: {
    //*----------------------------
    // 各パネルの初期化
    //*----------------------------
    initialize(paramId) {
      this.paramId = paramId;

      //* パラメータセットリストの取得
      useParamSetList(this.modelId)
        .then((response) => {
          console.log(response);
          const paramSetList = response["data"].data;
          this.parameterSetList.length = 0;
          for(const item of paramSetList){
            this.parameterSetList.push(item);
          }
          this.$nextTick(
            function () {
              this.$refs.refParameterSets.initialize(this.paramId);
            }.bind(this)
          );
        })
        .catch((error) => {
          console.log(error);
        });


    },
    //*----------------------------
    // 編集モードの初期化
    //*----------------------------
    setEditable(mode) {
      this.isEditMode = mode; // 子コンポーネントに伝達
      // 編集モードの場合
      if (this.isEditMode) {
        if (null == this.afterParameterSetData) {
          this.afterParameterSetData = Object.assign({},this.beforeParameterSetData);
        }
      }
    },
    //*----------------------------
    // 子コンポーネントからの詳細データの取得
    //*----------------------------
    updateData(data){
      this.beforeParameterSetData = data;
      this.afterParameterSetData = Object.assign({},this.beforeParameterSetData);
    },
    //*----------------------------
    // 子コンポーネントからの追加実施
    //*----------------------------
    addData(id){
     this.initialize(id);
    },
    //*----------------------------
    // パラメータの選択の変更
    //*----------------------------
    getItem(item) {
      this.paramId = item;
      this.$refs.refParameterSets.initialize(this.paramId);
      this.$emit("changeItem", this.paramId);
    },
    //*----------------------------
    // 上書き保存
    //*----------------------------
    overwriteSave() {
      this.$refs.refParameterSets.updateData(this.afterParameterSetData); // 更新処理
      this.$emit("changeItem", this.paramId);
    },
    //*----------------------------
    // 追加処理
    //*----------------------------
    aliasSave() {
      this.$refs.refParameterSetName.display();
    },
    //*----------------------------
    // 名前設定
    //*----------------------------
    //パラメータセット名の外部から制御
    handleSubmitParameterSetName: function (name) {
      this.afterParameterSetData.parameterName = name;
      this.$refs.refParameterSets.addData(this.afterParameterSetData); // 追加処理
      this.$refs.refParameterSetName.close();
      this.$emit("changeItem", this.paramId);
    },
    //*----------------------------
    // 削除
    //*----------------------------
    deleteParameterSet() {
      //* パラメータセットの取得
      useParamSetDelete(this.paramId)
        .then((response) => {
          const results = response["data"];
          if (results.status != 0) {
            console.log(results.message);
            alert("パラメータセットの削除に失敗しました。");
          } else {
            this.$emit("reset");
          }
        })
        .catch((error) => {
          console.log(error);
        });
    },
    //*----------------------------
    // 閉じるアクション
    //*----------------------------
    close() {
      this.$emit("close");
    },

    onDisable() {
      this.isDisabledDeleteBtn = true;
    },
  },
};
</script>

<style>
.photosynthesis .v-input__control {
  width: 50px;
  padding: 0%;
}

.photosynthesis .v-input__slot {
  padding: 10px;
}

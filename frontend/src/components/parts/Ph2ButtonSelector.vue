<template>
  <v-container>
    <v-row>
      <v-col>
        <v-card elevation="0" class="ma-1">
          <div :style="bindTitleStyle()" class="pa-3">{{ label }}</div>
          <v-slide-group class="pa-1" :multiple="multiple" show-arrows>
            <v-slide-item
              v-for="(item, index) in list"
              :key="index"
              v-slot:default="{ active }"
            >
              <div>
                <v-btn
                  class="ma-1"
                  color="primary"
                  :active="item['active']"
                  :outlined="item['active'] ? false : true"
                  depressed
                  @click="onCardClick(item, index, active)"
                >
                  {{ "title" in item ? item.title : item.name }}
                </v-btn>
              </div>
            </v-slide-item>
          </v-slide-group>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  props: {
    title: {
      type: String,
      required: true,
    },
    titleWidth: {
      type: Number,
      required: false,
      default: 100,
    },
    data: {
      type: Array,
      required: true,
    },
    handleClick: {
      type: Function,
      required: true,
    },
    multiple: {
      type: Boolean,
      default: false,
    },
  },

  data() {
    return {
      label: this.title != null ? this.title + ":" : null,
      list: this.data,
    };
  },

  watch: {},
  methods: {
    //* --------------------------------------------
    //* ボタン選択時の処理
    //* --------------------------------------------
    onCardClick(item, index, active) {
      if (this.multiple == false) {
        for (const element of this.list) {
          if (element === item) continue;
          element["active"] = false;
        }
      }
      if (item["active"]) {
        item["active"] = false;
      } else {
        item["active"] = true;
      }
      active = item["active"];
      this.handleClick(item, index, active);
    },

    bindTitleStyle() {
      return `float: left; width: ${this.titleWidth}px; `;
    },

    //選択されているボタンの初期化
    initializeButtonState() {
      if (this.list != null) {
        this.list.forEach((item) => {
          item["active"] = false;
        });
      }
    },
  },
};
</script>

<style lang="scss"></style>

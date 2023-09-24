<template>
  <div class="text-center;width:fit-content;margin">
    <v-menu offset-y>
      <template v-slot:activator="{ on, attrs }">
        <v-btn
          color="primary"
          dark
          v-bind="attrs"
          v-on="on"
          :disabled="items.length == 0"
          style="margin-left: 3%"
        >
          非表示の圃場
        </v-btn>
      </template>
      <v-list>
        <v-list-item v-for="(item, index) in items" :key="index">
          <v-list-item-title @click="selectItem(index)">{{
            item.title
          }}</v-list-item-title>
        </v-list-item>
      </v-list>
    </v-menu>
  </div>
</template>
  <script>
export default {
  data: () => ({
    items: [],
  }),

  methods: {
    selectItem: function (index) {
      var item = this.items[index];
      this.items.splice(index, 1);
      this.$emit("onClick", item);
    },
  },
};
</script>
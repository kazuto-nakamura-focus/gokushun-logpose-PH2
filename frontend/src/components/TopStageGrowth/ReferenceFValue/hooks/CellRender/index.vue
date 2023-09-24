<template>
    <span>
        <button class="action-button" data-action="delete">
            削除
        </button>
    </span>
</template>
<script>

export default {
    name: 'CellRender',
}
</script>

<style lang="scss" scoped>
.action-button {
    border: none;
    color: #ffffff;
    padding: 0px 12px;
    text-align: center;
    text-decoration: none;
    display: inline-block;
    font-size: 14px;
    opacity: 1;
    background-color: rgb(0, 180, 40);
    border-radius:5px;
}
</style>
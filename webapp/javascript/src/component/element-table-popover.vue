<template>
    <div>
        <el-popover placement="top" trigger="hover">
            <p>最新一次更改时间: {{moment(row.editTime).format("LLLL")}}</p>
            <div class="name-wrapper" slot="reference" v-text="data"></div>
        </el-popover>
    </div>
</template>
<script>
    import "moment/locale/zh-cn";
    import moment from "moment";

    export default {
        name: "elementTablePopover",
        props: ["data", "row"],
        methods: {
            moment
        }
    };
</script>
<style scoped></style>

<template>
    <div>
        <el-popover placement="top" trigger="hover">
            <p>最新一次修改时间: {{new Date(row.updateTime).getTime() === 1000 ? "从未修改" : moment(row.updateTime).format("YYYY年M月D日 Ah时m分")}}</p>
            <div class="name-wrapper" slot="reference" v-text="data"></div>
        </el-popover>
    </div>
</template>
<script>
    import "moment/locale/zh-cn";
    import moment from "moment";

    export default {
        name: "elementTablePopover",
        data() {
            return {};
        },
        props: ["data", "row"],
        methods: {
            moment
        }
    };
</script>
<style scoped></style>

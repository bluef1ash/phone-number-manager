import "@baseSrc/javascript/common/public";
import "@baseSrc/javascript/common/sidebar";
import Vue from "vue";
import VueRouter from "vue-router";
import VueCookie from "vue-cookie";
import {
    Button,
    Cascader,
    Loading,
    Message,
    MessageBox,
    Pagination,
    Popover,
    Table,
    TableColumn,
    Tree,
    Upload
} from "element-ui";
import listComponent from "@baseSrc/javascript/component/list";

$(document).ready(() => {
    Vue.use(VueRouter);
    Vue.prototype.$cookie = VueCookie;
    Vue.use(VueCookie);
    Vue.prototype.$message = Message;
    Vue.directive(MessageBox.name, MessageBox);
    Vue.prototype.$loading = Loading;
    Vue.prototype.$confirm = MessageBox.confirm;
    Vue.use(Tree);
    Vue.use(Pagination);
    Vue.use(Upload);
    Vue.use(Button);
    Vue.use(Loading);
    Vue.use(Table);
    Vue.use(TableColumn);
    Vue.use(Cascader);
    Vue.use(Popover);
    new Vue({
        el: "#dormitory_list",
        data: {},
        router: new VueRouter([
            {name: "home", path: "/:page", component: listComponent}
        ]),
        render: c => c(listComponent)
    })
    ;
})
;

define("learun", ["jquery", "bootstrap", "layui"], function () {
    var pageTabsContent = $(".page-tabs-content");
    var contentTabs = $(".content-tabs");
    var contentMain = $("#content_main");
    var mObject = {
        /**
         * 设置全屏
         */
        requestFullScreen: function () {
            var de = document.documentElement;
            if (de.requestFullscreen) {
                de.requestFullscreen();
            } else if (de.mozRequestFullScreen) {
                de.mozRequestFullScreen();
            } else if (de.webkitRequestFullScreen) {
                de.webkitRequestFullScreen();
            }
        },
        /**
         * 退出全屏
         */
        exitFullscreen: function () {
            var de = document;
            if (de.exitFullscreen) {
                de.exitFullscreen();
            } else if (de.mozCancelFullScreen) {
                de.mozCancelFullScreen();
            } else if (de.webkitCancelFullScreen) {
                de.webkitCancelFullScreen();
            }
        },
        /**
         * 刷新标签页
         */
        refreshTab: function () {
            var currentId = pageTabsContent.find(".active").attr("data-id");
            var target = $('.lradms-iframe[data-id="' + currentId + '"]');
            var url = target.attr("src");
            // $.loading(true);
            target.attr("src", url).on("load", function () {
                // $.loading(false);
            });
        },
        /**
         * 切换标签页
         */
        activeTab: function () {
            var currentId = $(this).data("id");
            if (!$(this).hasClass("active")) {
                contentMain.find(".lradms-iframe").each(function () {
                    if ($(this).data("id") == currentId) {
                        $(this).show().siblings(".lradms-iframe").hide();
                    }
                });
                $(this).addClass("active").siblings(".menu-tab").removeClass("active");
                mObject.scrollToTab(this);
            }
        },
        /**
         * 关闭其它标签页
         */
        closeOtherTabs: function () {
            pageTabsContent.children("[data-id]").find(".fa-remove").parents("a").not(".active").each(function () {
                $('.lradms-iframe[data-id="' + $(this).data("id") + '"]').remove();
                $(this).remove();
            });
            pageTabsContent.css("margin-left", "0");
        },
        /**
         * 关闭标签页
         * @returns {boolean}
         */
        closeTab: function () {
            var closeTabId = $(this).parent(".menu-tab").data("id");
            var currentWidth = $(this).parent(".menu-tab").width();
            if ($(this).parent(".menu-tab").hasClass("active")) {
                var activeId = null;
                if ($(this).parent(".menu-tab").next(".menu-tab").length) {
                    activeId = $(this).parent(".menu-tab").next(".menu-tab:eq(0)").data("id");
                    $(this).parent(".menu-tab").next(".menu-tab:eq(0)").addClass("active");
                    contentMain.find(".lradms-iframe").each(function () {
                        if ($(this).data("id") == activeId) {
                            $(this).show().siblings(".lradms-iframe").hide();
                            return false;
                        }
                    });
                    var marginLeftVal = parseInt(pageTabsContent.css("margin-left"));
                    if (marginLeftVal < 0) {
                        $(".page-tabs-content").animate({
                            marginLeft: (marginLeftVal + currentWidth) + "px"
                        }, "fast");
                    }
                    $(this).parents(".menu-tab").remove();
                    $("#content_main").find(".lradms-iframe").each(function () {
                        if ($(this).data("id") == closeTabId) {
                            $(this).remove();
                            return false;
                        }
                    });
                }
                if ($(this).parents(".menu-tab").prev(".menu-tab").length) {
                    activeId = $(this).parents(".menu-tab").prev(".menu-tab:last").data("id");
                    $(this).parents(".menu-tab").prev(".menu-tab:last").addClass("active");
                    contentMain.find(".lradms-iframe").each(function () {
                        if ($(this).data("id") == activeId) {
                            $(this).show().siblings(".lradms-iframe").hide();
                            return false;
                        }
                    });
                    $(this).parents(".menu-tab").remove();
                    contentMain.find(".lradms-iframe").each(function () {
                        if ($(this).data("id") == closeTabId) {
                            $(this).remove();
                            return false;
                        }
                    });
                }
            } else {
                $(this).parents(".menu-tab").remove();
                contentMain.find(".lradms-iframe").each(function () {
                    if ($(this).data("id") == closeTabId) {
                        $(this).remove();
                        return false;
                    }
                });
                mObject.scrollToTab($(".menu-tab.active"));
            }
            return false;
        },
        /**
         * 添加标签页
         * @returns {boolean}
         */
        addTab: function () {
            var menuTab = $(".menu-tab");
            $(".lradms-iframe").each(function (index, element) {
                var loginUrlLocal = basePath + "login.action";
                if ($(element).prop("src") == loginUrlLocal) {
                    location.href = loginUrlLocal;
                }
            });
            $(".navbar-custom-menu > ul > li.open").removeClass("open");
            var dataId = $(this).data("id");
            if (dataId != "") {
                //top.$.cookie("nfine_currentmoduleid", dataId, { path: "/" });
            }
            var dataUrl = $(this).prop("href");
            var menuName = $.trim($(this).text());
            var flag = true;
            if (dataUrl == undefined || $.trim(dataUrl).length == 0) {
                return false;
            }
            menuTab.each(function () {
                if ($(this).data("id") == dataUrl) {
                    if (!$(this).hasClass("active")) {
                        $(this).addClass("active").siblings(".menu-tab").removeClass("active");
                        mObject.scrollToTab(mObject);
                        $("#content_main").find(".lradms-iframe").each(function () {
                            if ($(this).data("id") == dataUrl) {
                                $(this).show().siblings(".lradms-iframe").hide();
                                return false;
                            }
                        });
                    }
                    flag = false;
                    return false;
                }
            });
            if (flag) {
                var str = '<a href="javascript:;" class="active menu-tab" data-id="' + dataUrl + '">' + menuName + ' <i class="fa fa-remove"></i></a>';
                menuTab.removeClass('active');
                var str1 = '<iframe class="lradms-iframe" id="iframe' + dataId + '" name="iframe' + dataId + '"  width="100%" height="100%" src="' + dataUrl + '" frameborder="0" data-id="' + dataUrl + '" seamless></iframe>';
                var contentMain = $("#content_main");
                contentMain.find("iframe.lradms-iframe").hide();
                contentMain.append(str1);
                //$.loading(true);
                contentMain.find("iframe:visible").on("load", function () {
                    //$.loading(false);
                });
                $(".menu-tabs .page-tabs-content").append(str);
                mObject.scrollToTab($(".menu-tab.active"));
                $("iframe#iframe" + dataId).on("load", function () {
                    $(this).contents().on("click", ".menu-tab", mObject.addTab);
                });
            }
            return false;
        },
        /**
         * 向右滚动标签页
         * @returns {boolean}
         */
        scrollTabRight: function () {
            var marginLeftVal = Math.abs(parseInt(pageTabsContent.css("margin-left")));
            var tabOuterWidth = mObject.calSumWidth(contentTabs.children().not(".menu-tabs"));
            var visibleWidth = contentTabs.outerWidth(true) - tabOuterWidth;
            var scrollVal = 0;
            if (pageTabsContent.width() < visibleWidth) {
                return false;
            } else {
                var tabElement = $(".menu-tab:first");
                var offsetVal = 0;
                while ((offsetVal + $(tabElement).outerWidth(true)) <= marginLeftVal) {
                    offsetVal += $(tabElement).outerWidth(true);
                    tabElement = $(tabElement).next();
                }
                offsetVal = 0;
                while ((offsetVal + $(tabElement).outerWidth(true)) < (visibleWidth) && tabElement.length > 0) {
                    offsetVal += $(tabElement).outerWidth(true);
                    tabElement = $(tabElement).next();
                }
                scrollVal = mObject.calSumWidth($(tabElement).prevAll());
                if (scrollVal > 0) {
                    pageTabsContent.animate({
                        marginLeft: 0 - scrollVal + "px"
                    }, "fast");
                }
            }
        },
        /**
         * 向左滚动标签页
         * @returns {boolean}
         */
        scrollTabLeft: function () {
            var marginLeftVal = Math.abs(parseInt(pageTabsContent.css("margin-left")));
            var tabOuterWidth = mObject.calSumWidth(contentTabs.children().not(".menu-tabs"));
            var visibleWidth = contentTabs.outerWidth(true) - tabOuterWidth;
            var scrollVal = 0;
            if (pageTabsContent.width() < visibleWidth) {
                return false;
            } else {
                var tabElement = $(".menu-tab:first");
                var offsetVal = 0;
                while ((offsetVal + $(tabElement).outerWidth(true)) <= marginLeftVal) {
                    offsetVal += $(tabElement).outerWidth(true);
                    tabElement = $(tabElement).next();
                }
                offsetVal = 0;
                if (mObject.calSumWidth($(tabElement).prevAll()) > visibleWidth) {
                    while ((offsetVal + $(tabElement).outerWidth(true)) < (visibleWidth) && tabElement.length > 0) {
                        offsetVal += $(tabElement).outerWidth(true);
                        tabElement = $(tabElement).prev();
                    }
                    scrollVal = mObject.calSumWidth($(tabElement).prevAll());
                }
            }
            pageTabsContent.animate({
                marginLeft: 0 - scrollVal + "px"
            }, "fast");
        },
        /**
         * 滚动到标签页
         * @param element
         */
        scrollToTab: function (element) {
            var marginLeftVal = mObject.calSumWidth($(element).prevAll()),
                marginRightVal = mObject.calSumWidth($(element).nextAll());
            var tabOuterWidth = mObject.calSumWidth(contentTabs.children().not(".menu-tabs"));
            var visibleWidth = contentTabs.outerWidth(true) - tabOuterWidth;
            var scrollVal = 0;
            if (pageTabsContent.outerWidth() < visibleWidth) {
                scrollVal = 0;
            } else if (marginRightVal <= (visibleWidth - $(element).outerWidth(true) - $(element).next().outerWidth(true))) {
                if ((visibleWidth - $(element).next().outerWidth(true)) > marginRightVal) {
                    scrollVal = marginLeftVal;
                    var tabElement = element;
                    while ((scrollVal - $(tabElement).outerWidth()) > (pageTabsContent.outerWidth() - visibleWidth)) {
                        scrollVal -= $(tabElement).prev().outerWidth();
                        tabElement = $(tabElement).prev();
                    }
                }
            } else if (marginLeftVal > (visibleWidth - $(element).outerWidth(true) - $(element).prev().outerWidth(true))) {
                scrollVal = marginLeftVal - $(element).prev().outerWidth(true);
            }
            pageTabsContent.animate({
                marginLeft: 0 - scrollVal + "px"
            }, "fast");
        },
        /**
         * 计算总宽度
         * @param element
         * @returns {number}
         */
        calSumWidth: function (element) {
            var width = 0;
            $(element).each(function () {
                width += $(this).outerWidth(true);
            });
            return width;
        },
        /**
         * 初始化
         */
        init: function () {
            var documentBody = $(document.body);
            var mainHeaderHeight = $(".main-header").height();
            var footerHeight = $(".footer").height();
            var mainSidebar = $(".main-sidebar");
            contentMain.height(documentBody.height() - mainHeaderHeight - contentTabs.height() - footerHeight - 5);
            mainSidebar.height(documentBody.height() - mainHeaderHeight - footerHeight - 5);
            /**
             * 设置导航栏点击事件
             */
            $(".menu-item").on("click", mObject.addTab);
            /**
             * 标签栏以及标签关闭按钮点击事件
             */
            $(".menu-tabs").on("click", ".menu-tab i", mObject.closeTab).on("click", ".menu-tab", mObject.activeTab);
            /**
             * 标签栏向左滚动按钮点击事件
             */
            $("#tab_left").on("click", mObject.scrollTabLeft);
            /**
             * 标签栏向右滚动按钮点击事件
             */
            $("#tab_right").on("click", mObject.scrollTabRight);
            /**
             * 单个刷新按钮点击事件
             */
            $("#tab_reload").on("click", mObject.refreshTab);
            /**
             * 关闭当前按钮点击事件
             */
            $("#tab_close_current").on("click", function () {
                pageTabsContent.find(".active i").trigger("click");
            });
            /**
             * 关闭全部按钮点击事件
             */
            $("#tab_close_all").on("click", function () {
                pageTabsContent.children("[data-id]").find(".fa-remove").each(function (index, element) {
                    $('.lradms-iframe[data-id="' + $(element).data("id") + '"]').remove();
                    $(element).parents("a").remove();
                });
                pageTabsContent.children("[data-id]:first").each(function (index, element) {
                    $('.lradms-iframe[data-id="' + $(element).data("id") + '"]').show();
                    $(element).addClass("active");
                });
                pageTabsContent.css("margin-left", "0");
            });
            /**
             * 关闭其它标签按钮点击事件
             */
            $("#tab_close_other").on("click", mObject.closeOtherTabs);
            /**
             * 全屏显示按钮点击事件
             */
            $("#fullscreen").on("click", function () {
                if (!$(this).attr("fullscreen")) {
                    $(this).attr("fullscreen", "true");
                    mObject.requestFullScreen();
                } else {
                    $(this).removeAttr("fullscreen");
                    mObject.exitFullscreen();
                }
            });
            /**
             * iframe框架内链接点击创建新标签
             */
            $(".lradms-iframe").on("load", function () {
                $(this).contents().on("click", ".menu-tab", mObject.addTab);
            });
            /**
             * 窗口变化事件
             */
            $(window).on("resize", function (e) {
                contentMain.height(documentBody.height() - mainHeaderHeight - $(".content-tabs").height() - footerHeight - 5);
                mainSidebar.height(documentBody.height() - mainHeaderHeight - footerHeight - 5);
            });
            /**
             * 展开或者缩小左侧菜单栏
             */
            $(".sidebar-toggle").on("click", function () {
                var loader = $("#ajax_loader");
                loader.css("display", "block");
                var contentWrapper = $("#content-wrapper");
                var logo = $(".logo");
                var navbar = $(".navbar");
                var body = $("body");
                if (!body.hasClass("sidebar-collapse")) {
                    // 缩小
                    body.addClass("sidebar-collapse");
                    mainSidebar.removeClass("col-md-2").addClass("col-md-1");
                    contentWrapper.removeClass("col-md-10").addClass("col-md-11");
                    logo.removeClass("col-md-2").addClass("col-md-1");
                    navbar.removeClass("col-md-10").addClass("col-md-11");
                } else {
                    // 展开
                    body.removeClass("sidebar-collapse");
                    mainSidebar.removeClass("col-md-1").addClass("col-md-2");
                    contentWrapper.removeClass("col-md-11").addClass("col-md-10");
                    logo.removeClass("col-md-1").addClass("col-md-2");
                    navbar.removeClass("col-md-11").addClass("col-md-10");
                }
                setTimeout(function () {
                    loader.fadeOut();
                }, 500);
            });

            /**
             * 设置导航栏搜索按钮
             */
            $("#search_btn").on("click", function () {
                var sidebarLiA = $("#sidebar-menu").find("li a");
                sidebarLiA.each(function (index, element) {
                    $(element).html($(element).html().replace(/<span\s*class="search-key">(.*?)<\/span>/gi, "$1"));
                }).show();
                var searchKey = $("input[name='search_key']").val();
                if (searchKey != "") {
                    var htmlKey = null;
                    var displayBtn = [];
                    sidebarLiA.each(function (index, element) {
                        htmlKey = $(element).html().replace(/<[^>]+>/g, "");
                        if (htmlKey.indexOf(searchKey) > -1) {
                            displayBtn.push(index);
                        }
                    });
                    if (displayBtn.length > 0) {
                        sidebarLiA.hide();
                        var obj = null;
                        var htmlK = null;
                        for (var i = 0; i < displayBtn.length; i++) {
                            obj = sidebarLiA.get(displayBtn[i]);
                            if ($(obj).hasClass("menu-item")) {
                                $(obj).parent().parent().prev().show();
                            }
                            htmlK = $(obj).html().replace(new RegExp(searchKey, "g"), '<span class="search-key">' + searchKey + '</span>');
                            $(obj).html(htmlK).show();
                        }
                    }
                }
            });

            /**
             * 搜索栏输入即开始搜索
             */
            $("input[name='search_key']").on("keyup", function (event) {
                if (event.which == 13) {
                    $("#search_btn").trigger("click");
                } else {
                    setTimeout(function () {
                        $("#search_btn").trigger("click");
                    }, 5000);
                }
            });
        },
        /**
         * 加载
         */
        load: function () {
            $(function () {
                /**
                 * 加载侧边栏导航菜单
                 */
                mObject.loadMenu();

                /**
                 * 页面初始化
                 */
                mObject.init();
                $("body").removeClass("hold-transition");
                setTimeout(function () {
                    $("#ajax_loader").fadeOut();
                }, 500);
                /**
                 * 清理缓存点击事件
                 */
                $("#clean_cache").click(function () {
                    layui.use("layer", function () {
                        var layer = layui.layer;
                        var layerIndex;
                        $.ajax({
                            url: basePath,
                            async: false,
                            beforeSend: function (xmlHttp) {
                                layerIndex = layer.load(1, {shade: [0.3, "#000"]});
                                xmlHttp.setRequestHeader("If-Modified-Since","0");
                                xmlHttp.setRequestHeader("Cache-Control","no-cache");
                            },
                            success: function () {
                                setTimeout(function () {
                                    layer.close(layerIndex);
                                    layer.msg("清理缓存完成！", {icon: 6});
                                }, 1000);
                            }
                        });
                    });
                });
            });
        },
        /**
         * 处理JSON
         * @param data
         * @param action
         * @returns {Array}
         */
        jsonWhere: function (data, action) {
            if (action === null) {
                return null;
            }
            var reval = [];
            $(data).each(function (i, v) {
                if (action(v)) {
                    reval.push(v);
                }
            });
            return reval;
        },
        /**
         * 加载菜单
         */
        loadMenu: function () {
            var _html = "";
            $.ajax({
                "url": basePath + "index/getmenu.action",
                "type": "get",
                "async": false,
                "cache": false,
                "data": {"isDisplay": 1},
                "success": function (data, status) {
                    if (status == "success" && data != null) {
                        var userPrivileges = data.userPrivileges;
                        for (var i = 0; i < userPrivileges.length; i++) {
                            _html += '<li class="treeview';
                            if (userPrivileges[i].constraintAuth == "residentAction") {
                                _html += ' active';
                            }
                            _html += '"><a href="javascript:;" ondragstart="return false;" title="' + userPrivileges[i].privilegeName + '"><i class="' + userPrivileges[i].iconName + '"></i><span>' + userPrivileges[i].privilegeName + '</span><i class="fa fa-angle-left pull-right"></i></a>';
                            if (userPrivileges[i].subUserPrivileges != null) {
                                _html += '<ul class="treeview-menu">';
                                for (var j = 0; j < userPrivileges[i].subUserPrivileges.length; j++) {
                                    _html += '<li><a class="menu-item" href="' + basePath + userPrivileges[i].subUserPrivileges[j].uri.substring(1) + '.action" data-id="' + userPrivileges[i].subUserPrivileges[j].privilegeId + '" ondragstart="return false;" title="' + userPrivileges[i].subUserPrivileges[j].privilegeName + '"><i class="' + userPrivileges[i].subUserPrivileges[j].iconName + '"></i>' + userPrivileges[i].subUserPrivileges[j].privilegeName + "</a></li>";
                                }
                                _html += "</ul>";
                            }
                            _html += "</li>";
                        }
                    }
                }
            });
            /**
             * 设置菜单打开或关闭
             */
            $("#sidebar-menu").append(_html).children("li").children("a").click(function () {
                var d = $(this), e = d.next();
                if (e.is(".treeview-menu") && e.is(":visible")) {
                    e.slideUp(500, function () {
                        e.removeClass("menu-open")
                    });
                    e.parent("li").removeClass("active");
                } else if (e.is(".treeview-menu") && !e.is(":visible")) {
                    var f = d.parents("ul").first();
                    var g = f.find("ul:visible").slideUp(500);
                    g.removeClass("menu-open");
                    var h = d.parent("li");
                    e.slideDown(500, function () {
                        e.addClass("menu-open");
                        f.find("li.active").removeClass("active");
                        h.addClass("active");
                        var sidebarMenu = $("#sidebar-menu");
                        var _height1 = $(window).height() - sidebarMenu.children("li.active").position().top - 41;
                        var _height2 = sidebarMenu.find("li").children("ul.menu-open").height() + 10;
                        if (_height2 > _height1) {
                            sidebarMenu.children("li").children("ul.menu-open").css({
                                overflow: "auto",
                                height: _height1
                            });
                        }
                    });
                }
                e.is(".treeview-menu");
            });
        }
    };
    return mObject.load;
});

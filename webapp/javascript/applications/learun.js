define("learun", ["jquery"], function () {
    var aEl = null;
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
            var currentId = $('.page-tabs-content').find('.active').attr('data-id');
            var target = $('.LRADMS_iframe[data-id="' + currentId + '"]');
            var url = target.attr('src');
            // $.loading(true);
            target.attr('src', url).on("load", function () {
                // $.loading(false);
            });
        },
        /**
         * 切换标签页
         */
        activeTab: function () {
            var currentId = $(this).data('id');
            if (!$(this).hasClass('active')) {
                $('.mainContent .LRADMS_iframe').each(function () {
                    if ($(this).data('id') == currentId) {
                        $(this).show().siblings('.LRADMS_iframe').hide();
                    }
                });
                $(this).addClass('active').siblings('.menuTab').removeClass('active');
                mObject.scrollToTab(this);
            }
        },
        /**
         * 关闭其它标签页
         */
        closeOtherTabs: function () {
            $('.page-tabs-content').children("[data-id]").find('.fa-remove').parents('a').not(".active").each(function () {
                $('.LRADMS_iframe[data-id="' + $(this).data('id') + '"]').remove();
                $(this).remove();
            });
            $('.page-tabs-content').css("margin-left", "0");
        },
        /**
         * 关闭标签页
         * @returns {boolean}
         */
        closeTab: function () {
            var closeTabId = $(this).parent(".menuTab").data('id');
            var currentWidth = $(this).parent(".menuTab").width();
            if ($(this).parent(".menuTab").hasClass('active')) {
                if ($(this).parent(".menuTab").next('.menuTab').length) {
                    var activeId = $(this).parent(".menuTab").next('.menuTab:eq(0)').data('id');
                    $(this).parent(".menuTab").next('.menuTab:eq(0)').addClass('active');
                    $('.mainContent .LRADMS_iframe').each(function () {
                        if ($(this).data('id') == activeId) {
                            $(this).show().siblings('.LRADMS_iframe').hide();
                            return false;
                        }
                    });
                    var marginLeftVal = parseInt($('.page-tabs-content').css('margin-left'));
                    if (marginLeftVal < 0) {
                        $('.page-tabs-content').animate({
                            marginLeft: (marginLeftVal + currentWidth) + 'px'
                        }, "fast");
                    }
                    $(this).parents('.menuTab').remove();
                    $('.mainContent .LRADMS_iframe').each(function () {
                        if ($(this).data('id') == closeTabId) {
                            $(this).remove();
                            return false;
                        }
                    });
                }
                if ($(this).parents('.menuTab').prev('.menuTab').length) {
                    var activeId = $(this).parents('.menuTab').prev('.menuTab:last').data('id');
                    $(this).parents('.menuTab').prev('.menuTab:last').addClass('active');
                    $('.mainContent .LRADMS_iframe').each(function () {
                        if ($(this).data('id') == activeId) {
                            $(this).show().siblings('.LRADMS_iframe').hide();
                            return false;
                        }
                    });
                    $(this).parents('.menuTab').remove();
                    $('.mainContent .LRADMS_iframe').each(function () {
                        if ($(this).data('id') == closeTabId) {
                            $(this).remove();
                            return false;
                        }
                    });
                }
            } else {
                $(this).parents('.menuTab').remove();
                $('.mainContent .LRADMS_iframe').each(function () {
                    if ($(this).data('id') == closeTabId) {
                        $(this).remove();
                        return false;
                    }
                });
                mObject.scrollToTab($('.menuTab.active'));
            }
            return false;
        },
        /**
         * 添加标签页
         * @returns {boolean}
         */
        addTab: function () {
            $(".navbar-custom-menu>ul>li.open").removeClass("open");
            var dataId = $(this).data('id');
            if (dataId != "") {
                //top.$.cookie('nfine_currentmoduleid', dataId, { path: "/" });
            }
            var dataUrl = $(this).prop('href');
            var menuName = $.trim($(this).text());
            var flag = true;
            if (dataUrl == undefined || $.trim(dataUrl).length == 0) {
                return false;
            }
            $('.menuTab').each(function () {
                if ($(this).data('id') == dataUrl) {
                    if (!$(this).hasClass('active')) {
                        $(this).addClass('active').siblings('.menuTab').removeClass('active');
                        mObject.scrollToTab(mObject);
                        $('.mainContent .LRADMS_iframe').each(function () {
                            if ($(this).data('id') == dataUrl) {
                                $(this).show().siblings('.LRADMS_iframe').hide();
                                return false;
                            }
                        });
                    }
                    flag = false;
                    return false;
                }
            });
            if (flag) {
                var str = '<a href="javascript:;" class="active menuTab" data-id="' + dataUrl + '">' + menuName + ' <i class="fa fa-remove"></i></a>';
                $('.menuTab').removeClass('active');
                var str1 = '<iframe class="LRADMS_iframe" id="iframe' + dataId + '" name="iframe' + dataId + '"  width="100%" height="100%" src="' + dataUrl + '" frameborder="0" data-id="' + dataUrl + '" seamless></iframe>';
                $('.mainContent').find('iframe.LRADMS_iframe').hide();
                $('.mainContent').append(str1);
                //$.loading(true);
                $('.mainContent iframe:visible').on("load", function () {
                    //$.loading(false);
                });
                $('.menuTabs .page-tabs-content').append(str);
                mObject.scrollToTab($('.menuTab.active'));
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
            var marginLeftVal = Math.abs(parseInt($('.page-tabs-content').css('margin-left')));
            var tabOuterWidth = this.calSumWidth($(".content-tabs").children().not(".menuTabs"));
            var visibleWidth = $(".content-tabs").outerWidth(true) - tabOuterWidth;
            var scrollVal = 0;
            if ($(".page-tabs-content").width() < visibleWidth) {
                return false;
            } else {
                var tabElement = $(".menuTab:first");
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
                scrollVal = this.calSumWidth($(tabElement).prevAll());
                if (scrollVal > 0) {
                    $('.page-tabs-content').animate({
                        marginLeft: 0 - scrollVal + 'px'
                    }, "fast");
                }
            }
        },
        /**
         * 向左滚动标签页
         * @returns {boolean}
         */
        scrollTabLeft: function () {
            var marginLeftVal = Math.abs(parseInt($('.page-tabs-content').css('margin-left')));
            var tabOuterWidth = this.calSumWidth($(".content-tabs").children().not(".menuTabs"));
            var visibleWidth = $(".content-tabs").outerWidth(true) - tabOuterWidth;
            var scrollVal = 0;
            if ($(".page-tabs-content").width() < visibleWidth) {
                return false;
            } else {
                var tabElement = $(".menuTab:first");
                var offsetVal = 0;
                while ((offsetVal + $(tabElement).outerWidth(true)) <= marginLeftVal) {
                    offsetVal += $(tabElement).outerWidth(true);
                    tabElement = $(tabElement).next();
                }
                offsetVal = 0;
                if (this.calSumWidth($(tabElement).prevAll()) > visibleWidth) {
                    while ((offsetVal + $(tabElement).outerWidth(true)) < (visibleWidth) && tabElement.length > 0) {
                        offsetVal += $(tabElement).outerWidth(true);
                        tabElement = $(tabElement).prev();
                    }
                    scrollVal = this.calSumWidth($(tabElement).prevAll());
                }
            }
            $('.page-tabs-content').animate({
                marginLeft: 0 - scrollVal + 'px'
            }, "fast");
        },
        /**
         * 滚动到标签页
         * @param element
         */
        scrollToTab: function (element) {
            var marginLeftVal = this.calSumWidth($(element).prevAll()),
                marginRightVal = this.calSumWidth($(element).nextAll());
            var tabOuterWidth = this.calSumWidth($(".content-tabs").children().not(".menuTabs"));
            var visibleWidth = $(".content-tabs").outerWidth(true) - tabOuterWidth;
            var scrollVal = 0;
            if ($(".page-tabs-content").outerWidth() < visibleWidth) {
                scrollVal = 0;
            } else if (marginRightVal <= (visibleWidth - $(element).outerWidth(true) - $(element).next().outerWidth(true))) {
                if ((visibleWidth - $(element).next().outerWidth(true)) > marginRightVal) {
                    scrollVal = marginLeftVal;
                    var tabElement = element;
                    while ((scrollVal - $(tabElement).outerWidth()) > ($(".page-tabs-content").outerWidth() - visibleWidth)) {
                        scrollVal -= $(tabElement).prev().outerWidth();
                        tabElement = $(tabElement).prev();
                    }
                }
            } else if (marginLeftVal > (visibleWidth - $(element).outerWidth(true) - $(element).prev().outerWidth(true))) {
                scrollVal = marginLeftVal - $(element).prev().outerWidth(true);
            }
            $('.page-tabs-content').animate({
                marginLeft: 0 - scrollVal + 'px'
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
            $('.menuItem').on('click', this.addTab);
            $('.menuTabs').on('click', '.menuTab i', this.closeTab).on('click', '.menuTab', this.activeTab);
            $('.tabLeft').on('click', this.scrollTabLeft);
            $('.tabRight').on('click', this.scrollTabRight);
            $('.tabReload').on('click', this.refreshTab);
            $('.tabCloseCurrent').on('click', function () {
                $('.page-tabs-content').find('.active i').trigger("click");
            });
            $('.tabCloseAll').on('click', function () {
                $('.page-tabs-content').children("[data-id]").find('.fa-remove').each(function () {
                    $('.LRADMS_iframe[data-id="' + $(this).data('id') + '"]').remove();
                    $(this).parents('a').remove();
                });
                $('.page-tabs-content').children("[data-id]:first").each(function () {
                    $('.LRADMS_iframe[data-id="' + $(this).data('id') + '"]').show();
                    $(this).addClass("active");
                });
                $('.page-tabs-content').css("margin-left", "0");
            });
            $('.tabCloseOther').on('click', this.closeOtherTabs);
            $('.fullscreen').on('click', function () {
                if (!$(this).attr('fullscreen')) {
                    $(this).attr('fullscreen', 'true');
                    mObject.requestFullScreen();
                } else {
                    $(this).removeAttr('fullscreen');
                    mObject.exitFullscreen();
                }
            });
        },
        /**
         * 加载
         */
        load: function () {
            var body = $("body");
            body.removeClass("hold-transition");
            $("#content-wrapper").find('.mainContent').height($(window).height() - 100);
            $(window).resize(function (e) {
                $("#content-wrapper").find('.mainContent').height($(window).height() - 100);
            });
            $(".sidebar-toggle").click(function () {
                if (!body.hasClass("sidebar-collapse")) {
                    body.addClass("sidebar-collapse");
                } else {
                    body.removeClass("sidebar-collapse");
                }
            });
            $("iframe.LRADMS_iframe").on("load", function () {
                $(this).contents().on("click", ".menu-tab", mObject.addTab);
            });
            window.setTimeout(function () {
                $('#ajax_loader').fadeOut();
            }, 300);
        },
        /**
         * 处理JSON
         * @param data
         * @param action
         * @returns {Array}
         */
        jsonWhere: function (data, action) {
            if (action === null) {
                return;
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
                            _html += '"><a href="javascript:;"><i class="' + userPrivileges[i].iconName + '"></i><span>' + userPrivileges[i].privilegeName + '</span><i class="fa fa-angle-left pull-right"></i></a>';
                            if (userPrivileges[i].subUserPrivileges != null) {
                                _html += '<ul class="treeview-menu">';
                                for (var j = 0; j < userPrivileges[i].subUserPrivileges.length; j++) {
                                    _html += '<li><a class="menuItem" href="' + basePath + userPrivileges[i].subUserPrivileges[j].uri.substring(1) + '" data-id="' + userPrivileges[i].subUserPrivileges[j].privilegeId + '"><i class="' + userPrivileges[i].subUserPrivileges[j].iconName + '"></i>' + userPrivileges[i].subUserPrivileges[j].privilegeName + '</a></li>';
                                }
                                _html += '</ul>';
                            }
                            _html += '</li>';
                        }
                    }
                }
            });
            $("#sidebar-menu").append(_html).children("li").children("a").click(function () {
                var d = $(this), e = d.next();
                if (e.is(".treeview-menu") && e.is(":visible")) {
                    e.slideUp(500, function () {
                        e.removeClass("menu-open")
                    });
                    e.parent("li").removeClass("active")
                } else if (e.is(".treeview-menu") && !e.is(":visible")) {
                    var f = d.parents("ul").first();
                    var g = f.find("ul:visible").slideUp(500);
                    g.removeClass("menu-open");
                    var h = d.parent("li");
                    e.slideDown(500, function () {
                        e.addClass("menu-open");
                        f.find("li.active").removeClass("active");
                        h.addClass("active");
                        var _height1 = $(window).height() - $("#sidebar-menu >li.active").position().top - 41;
                        var _height2 = $("#sidebar-menu li > ul.menu-open").height() + 10;
                        if (_height2 > _height1) {
                            $("#sidebar-menu >li > ul.menu-open").css({
                                overflow: "auto",
                                height: _height1
                            })
                        }
                    })
                }
                e.is(".treeview-menu");
            });
        }
    };
    return mObject;
});

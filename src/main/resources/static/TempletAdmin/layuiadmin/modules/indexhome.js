/**

 @Name：layuiAdmin 主页控制台
 @Author：贤心
 @Site：http://www.layui.com/admin/
 @License：GPL-2

 */





layui.define(function (exports) {

    /*
      下面通过 layui.use 分段加载不同的模块，实现不同区域的同时渲染，从而保证视图的快速呈现
    */


    //区块轮播切换
    layui.use(['admin', 'carousel'], function () {
        var $ = layui.$
            , admin = layui.admin
            , carousel = layui.carousel
            , element = layui.element
            , device = layui.device();

        //轮播切换
        $('.layadmin-carousel').each(function () {
            var othis = $(this);
            carousel.render({
                elem: this
                , width: '100%'
                , arrow: 'none'
                , interval: othis.data('interval')
                , autoplay: othis.data('autoplay') === true
                , trigger: (device.ios || device.android) ? 'click' : 'hover'
                , anim: othis.data('anim')
            });
        });

        element.render('progress');

    });

    //数据概览
    layui.use(['carousel', 'echarts'], function () {
        var $ = layui.$
            , carousel = layui.carousel
            , echarts = layui.echarts;


        var echartsApp = [], options = [
            //本月销售统计
            {
                title: {
                    text: '本月访问统计',
                    x: 'center',
                    textStyle: {
                        fontSize: 14
                    }
                },
                tooltip: {
                    trigger: 'axis'
                },
                legend: {
                    data: ['', '']
                },
                xAxis: [{
                    type: 'category',
                    boundaryGap: false,
                    data: monthDay
                }],
                yAxis: [{
                    type: 'value'
                }],
                series: [{
                    name: '访问次数',
                    type: 'line',
                    smooth: true,
                    itemStyle: {normal: {areaStyle: {type: 'default'}}},
                    data: monthAmount
                }]
            },
            //访客城市分布
            {
                title: {
                    text: '访问城市分析',
                    x: 'center',
                    textStyle: {
                        fontSize: 14
                    }
                },
                tooltip: {
                    trigger: 'item',
                    formatter: "{a} <br/>{b} : {c} ({d}%)"
                },
                legend: {
                    orient: 'vertical',
                    x: 'left',
                    data: cityNames
                },
                series: [{
                    name: '城市',
                    type: 'pie',
                    radius: '55%',
                    center: ['50%', '50%'],
                    data: cityCounts
                }]
            },
            //访客浏览器分布
            {
                title: {
                    text: '访问来源分析',
                    x: 'center',
                    textStyle: {
                        fontSize: 14
                    }
                },
                tooltip: {
                    trigger: 'item',
                    formatter: "{a} <br/>{b} : {c} ({d}%)"
                },
                legend: {
                    orient: 'vertical',
                    x: 'left',
                    data: browserNames
                },
                series: [{
                    name: '来源占比',
                    type: 'pie',
                    radius: '55%',
                    center: ['50%', '50%'],
                    data: browserCounts
                }]
            }
        ]
            , elemDataView = $('#LAY-index-dataview').children('div')
            , renderDataView = function (index) {
            echartsApp[index] = echarts.init(elemDataView[index], layui.echartsTheme);
            echartsApp[index].setOption(options[index]);
            window.onresize = echartsApp[index].resize;
        };


        //没找到DOM，终止执行
        if (!elemDataView[0]) return;


        renderDataView(0);

        //监听数据概览轮播
        var carouselIndex = 0;
        carousel.on('change(LAY-index-dataview)', function (obj) {
            renderDataView(carouselIndex = obj.index);
        });

        //监听侧边伸缩
        layui.admin.on('side', function () {
            setTimeout(function () {
                renderDataView(carouselIndex);
            }, 300);
        });

        //监听路由
        layui.admin.on('hash(tab)', function () {
            layui.router().path.join('') || renderDataView(carouselIndex);
        });
    });

    //最新订单
    layui.use('table', function () {
        var $ = layui.$
            , table = layui.table;

        //今日热搜
        table.render({
            elem: '#LAY-index-topSearch'
            , url: layui.setter.base + 'json/console/top-search.js' //模拟接口
            , page: true
            , cols: [[
                {type: 'numbers', fixed: 'left'}
                , {
                    field: 'keywords',
                    title: '关键词',
                    minWidth: 300,
                    templet: '<div><a href="https://www.baidu.com/s?wd={{ d.keywords }}" target="_blank" class="layui-table-link">{{ d.keywords }}</div>'
                }
                , {field: 'frequency', title: '搜索次数', minWidth: 120, sort: true}
                , {field: 'userNums', title: '用户数', sort: true}
            ]]
            , skin: 'line'
        });

        //今日热贴
        table.render({
            elem: '#LAY-index-topCard'
            , url: layui.setter.base + 'json/console/top-card.js' //模拟接口
            , page: true
            , cellMinWidth: 120
            , cols: [[
                {type: 'numbers', fixed: 'left'}
                , {
                    field: 'title',
                    title: '标题',
                    minWidth: 300,
                    templet: '<div><a href="{{ d.href }}" target="_blank" class="layui-table-link">{{ d.title }}</div>'
                }
                , {field: 'username', title: '发帖者'}
                , {field: 'channel', title: '类别'}
                , {field: 'crt', title: '点击率', sort: true}
            ]]
            , skin: 'line'
        });
    });

    exports('console', {})
});
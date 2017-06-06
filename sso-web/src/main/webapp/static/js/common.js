/**
 * Created by Administrator on 2017/3/25.
 */
var Common = {

    /**
     * 把form表单转换成json对象
     * @param $formObj
     * @returns {{}}
     */
    serializeJson: function (formId) {
        var $formObj = $("#"+formId);
        var serializeObj = {};
        var dataArray = [];

        if (typeof($formObj) != "undefined" && $formObj != null) {
            dataArray = $formObj.serializeArray();
        }

        if (typeof(dataArray) != "undefined" && dataArray != null && dataArray.length > 0) {
            $(dataArray).each(function () {
                if (serializeObj[this.name]) {
                    if ($.isArray(serializeObj[this.name])) {
                        serializeObj[this.name].push(this.value);
                    } else {
                        serializeObj[this.name] = [serializeObj[this.name], this.value];
                    }
                } else {
                    serializeObj[this.name] = this.value;
                }
            });
        }
        return serializeObj;
    },
}
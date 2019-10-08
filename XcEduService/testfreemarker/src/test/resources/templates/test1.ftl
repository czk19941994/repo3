<!DOCTYPE html>
<html>
<head>
    <meta charset="utf‐8">
    <title>Hello World!</title>
    </head>
    <body>
    Hello
        <table>
            <tr>
                <td>序号</td>
                <td>姓名</td>
                <td>年龄</td>
                <td>钱</td>
            </tr>
            <#--遍历list-->
            <#list list as stu>
                <tr>
                    <td>${stu_index}</td>
                    <td>${stu.name}</td>
                    <td>${stu.age}</td>
                    <td>${stu.money}</td>
                </tr>
            </#list><br/>
        </table>

    </body>
</html>
# SlimefunServerEssentials

一个可以导出 Slimefun 物品与配方的附属。导出的数据可以给[Slimefun Essentials 模组](https://github.com/SlimefunGuguProject/SlimefunEssentials)使用。

## 模组数据替换教程

### 服务端导出数据

服务端安装 SlimefunServerEssentials 后，可使用指令:
- `/slimefun_server_essentials export all_items` 导出服务器所有物品的数据
- `/slimefun_server_essentials export all_categories` 导出服务器所有分类的数据

导出的数据位于`/plugins/SlimefunServerEssentials/exported`。

### mod导入数据

1. 使用压缩软件打开mod文件，并打开`/assets/slimefun_essentials/slimefun`文件夹；
2. 将导出数据文件夹中的2个文件夹`categories`和`items`替换掉mod中相应的文件夹。

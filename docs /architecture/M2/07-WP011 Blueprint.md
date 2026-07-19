WP011 真正需要的不是 1 个文档，而是 7 个文档。
这是我现在最大的建议（也是整个项目未来最大的护城河）

我建议 M2-005《AI Mental Model》 不只是给 Rowboat 看，而是成为所有 AI 编程工具（Rowboat、Claude Code、Codex、Cursor 等）进入本项目时的第一份必读文档。

它不是描述"系统怎么运行"，而是定义：

"在这个项目里，AI 应该如何思考。

任何 AI 只有先接受这套思维模型，再去读 Blueprint 和写代码，才能持续输出与你希望一致的实现，而不是回到传统"服务器 + 指标 + 中间件"的思路。

我认为，这份文档会成为整个 AI Performance Engineer 项目最独特、也最难复制的资产之一。

M2
│
├── M2-000 AI World Model
│
├── M2-001 Domain Language Specification ⭐⭐⭐⭐⭐
│
├── M2-002 Resource Taxonomy
│
├── M2-003 Resource Discovery Specification
│
├── M2-004 Resource Lifecycle Specification
│
├── M2-005 AI Mental Model ⭐⭐⭐⭐⭐
│
└── WP011 Blueprint

为什么？

因为：

Blueprint：

只回答：

How

但是：

前面：

四份：

回答：

Why

What

我建议 Rowboat 的开发流程改一下

以前：

Blueprint

↓

Coding

以后：

变成：

AI World Model

↓

DSL

↓

Taxonomy

↓

Discovery

↓

Lifecycle

↓

Blueprint

↓

Coding

这样：

Rowboat：

就不会：

写着写着：

开始：

发明：

自己的：

Resource。
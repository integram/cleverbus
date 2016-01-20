# How to write wiki?

## Introduction

As long as we need to support GFM syntax which uses GiHub for our CE edition we have moved from Confluence to GFM. So that we have one source of documentation for both CE and EE edition. Versioning is done basically by git. For each released version of cleverbus we have also up-to-date documention in the same repo.

- Wiki pages are stored in GIT repo in doc/wiki folder
- Wiki pages are writen using [GitHub Flavored Markdown][A] syntax
- To modify/add content just use .md sources in this repo

### Tools for editing
- Text editor of your choice eg. [Notepad++][2]
	- no HTML preview
- In favorite development IDE (HTML preview)
	- [InteliJ MultiMarkdown plugin][3]
	- [NetBeans Flow Markdown plugin][4] 
- Specialized markdown editor eg. [MarkdownPad][5]
- Markdow table online [generator/editor][6]
	- with CSV file import


## Rules for documentation writing

-   Write new documentation in English only.
-   There is one version of documentation for all CleverBus versions. If some information is valid for or from specific version then highlight it. Example:

    From version 0.2
        
-   Use Markdown code macro for code snippets.
-   If you write Java classes, configuration parameters or use other technical names then use italic format style.


[1]: https://help.github.com/articles/github-flavored-markdown/
[2]: https://github.com/Edditoria/markdown_npp_zenburn
[3]: https://plugins.jetbrains.com/plugin/7896?pr=idea
[4]: https://github.com/madflow/flow-netbeans-markdown/releases
[5]: http://markdownpad.com/
[6]: http://www.tablesgenerator.com/markdown_tables

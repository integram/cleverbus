=begin
This file can be used to (e.g.):
- alter certain inner parts of Gollum,
- extend it with your stuff.

It is especially useful for customizing supported formats/markups. For more information and examples:
- https://github.com/gollum/gollum#config-file

=end

# enter your Ruby code here ...

DIV_STYLE = "border: 1px solid; margin: 15px 0px; padding: 6px 10px; border-radius: 3px;"

module Gollum
	class Macro

		class Approve < Gollum::Macro
			def render(content)
				"<div style=\"#{DIV_STYLE} background-color: rgb(240,250,240); border-color: rgb(120,180,120);\">#{content}</div>"
				end
			end

		class Warning < Gollum::Macro
			def render(content)
				"<div style=\"#{DIV_STYLE} background-color: rgb(250,240,240); border-color: rgb(180,120,120);\">#{content}</div>"
				end
			end

		class Info < Gollum::Macro
			def render(content)
				"<div style=\"#{DIV_STYLE} background-color: rgb(245,245,245); border-color: rgb(144,144,144);\">#{content}</div>"
			end
		end
	end
end

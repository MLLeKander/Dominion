
# usage: tmux -f presentation.tmux

bind-key -n C-r set status off \; splitw "sleep 0.5; echo Server; echo; java -cp ~/Dominion dominion481.server.GameServer" \; kill-pane -a \; splitw -h "sleep 1; java -cp ~/Dominion dominion481.server.GameClient" \; splitw -v "sleep 1.2; java -cp ~/Dominion dominion481.server.GameClient" \; select-pane -U
set -g mouse-resize-pane on
set -g mouse-select-pane on

# bash completion for navdb
have navdb &&
_navdb() {
    local cur prev words cword split=false
    local common_opts='-h --help -v --verbose -d --dbname -c --config'
    local all_cmds='drop dump export import restore setup'
    local i w opts kw_opts kw_index config_file last_index
    local kw_words

    COMPREPLY=()
    _get_comp_words_by_ref -n = cur prev words cword
    _split_longopt && split=true
    let last_index="${#words[@]}-1"

    # common options w/arguments
    case $prev in
        # navdb setup --load-file=FILENAME
        -F|--load-file)
            _filedir
            return 0
            ;;
        # argument required but no completion available
        -d|--dbname)
            return 0
            ;;
        # navdb --config=FILENAME ...
        -c|--config)
            _filedir
            return 0
            ;;
    esac
    $split && return 0

    # find non-option words
    for w in "${words[@]}" ; do
        [[ "$w" = -* ]] || kw_words="$kw_words $w"
    done

    # parse words
    kw_opts="$all_cmds"
    for ((i=0;$i<=${#words[@]};++i)) ; do
        w=${words[$i]}
        case "$w" in
            drop)
                opts="-f --force"
                kw_opts=""
                kw_index=$i
                break
                ;;
            dump)
                [[ "$cur" != -* ]] && { _filedir ; return 0 ; } || :
                opts="-Z --compress"
                kw_opts=""
                kw_index=$i
                break
                ;;
            export)
                [[ "$cur" != -* ]] && { _filedir ; return 0 ; } || :
                opts="--world --staging"
                kw_opts=""
                kw_index=$i
                break
                ;;
            import)
                [[ "$cur" != -* ]] && { _filedir ; return 0 ; } || :
                opts="--world --staging --force"
                kw_opts=""
                kw_index=$i
                break
                ;;
            restore)
                [[ "$cur" != -* ]] && { _filedir ; return 0 ; } || :
                opts="--no-vacuum --force"
                kw_opts=""
                kw_index=$i
                break
                ;;
            setup)
                opts="--nodata -F --load-file -M --data-model-version -P --upgrade-path"
                kw_opts=""
                kw_index=$i
                break
                ;;
        esac    
    done

    if [[ "$cur" = -* ]] ; then
        COMPREPLY=( $( compgen -W "$common_opts $opts" -- "$cur" ) )
    else
        if [ -n "$kw_index" ] && [ "$kw_index" -le "$cword" ] ; then
            kw_opts=$(_pgh_clean_list "$kw_opts" "$kw_words")
            COMPREPLY=( $( compgen -W "$kw_opts $common_opts $opts" -- "$cur" ) )
        elif [ "$cword" -ge "$last_index" ] ; then
            COMPREPLY=( $( compgen -W "$all_cmds" -- "$cur" ) )
        fi
    fi
    return 0
} &&
complete -F _navdb navdb

# Local variables:
# mode: shell-script
# sh-basic-offset: 4
# sh-indent-comment: t
# indent-tabs-mode: nil
# End:
# ex: ts=4 sw=4 et filetype=sh

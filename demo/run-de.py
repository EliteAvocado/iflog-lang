import os
import subprocess

# =================================================================================================================== #
# === CONSTANTS === #
# =================================================================================================================== #

SCREEN_WIDTH            = 120
LANG_DELIM              = "de"
IFLOG_JAR_PATH          = "../lib/Standalone"
IFLTARGET_FILE_PATH     = "../target/PostgreSQL"

NEW_ENV                 = os.environ.copy()
NEW_ENV["PGPASSWORD"]   = "postgres"
DB_NAME                 = "iflogdemodb"
DB_NOTICE_PREFIX        = "HINWEIS:"

IFLOG_JAR               = "IFLOG.jar"
IFLTARGET_FILE          = "PSQL.ifltarget"
IFLOG_FILE              = "demo.ifl"
SQL_OUTPUT_FILE         = "demo_psql.sql"
LOG_FILE                = "demo_log.txt"

IFLOG_JAR               = IFLOG_JAR_PATH        + "/" + IFLOG_JAR
IFLTARGET_FILE          = IFLTARGET_FILE_PATH   + "/" + IFLTARGET_FILE
IFLOG_FILE              = LANG_DELIM            + "/" + IFLOG_FILE
SQL_OUTPUT_FILE         = LANG_DELIM            + "/" + SQL_OUTPUT_FILE
LOG_FILE                = LANG_DELIM            + "/" + LOG_FILE

# =================================================================================================================== #
# === CONSOLE OUTPUT === #
# =================================================================================================================== #

def output_command(args, out_str, err_str, suppress_newline = False):
    command = ' '.join(args).replace("\n","\\n")
    print("\n" + SCREEN_WIDTH*"=")
    print("BEFEHL: " + command)
    print(SCREEN_WIDTH*"=")
    prefix_str = ""
    if not suppress_newline:
        prefix_str += "\n"
    if (len(out_str) + len(err_str)) == 0:
        print(prefix_str + "-")
    else:            
        if len(out_str) > 0:
            out_str = out_str.strip().removeprefix(DB_NOTICE_PREFIX).strip()
            print(prefix_str + out_str)
        if len(err_str) > 0:
            err_str = err_str.strip().removeprefix(DB_NOTICE_PREFIX).strip()  
            print(prefix_str + err_str)

def output_map():
    start_room = run_map_command("start_room")
    garden = run_map_command("garden")
    end_room = run_map_command("end_room")

    ret_val = subprocess.run(
        ["psql", "-U", "postgres", "-d", DB_NAME, "-qAt", "-c", "set client_encoding=win1252",
         "-c", "SELECT loc FROM character WHERE name = 'player'"],
         text=True,capture_output=True,env=NEW_ENV)
    cur_room = int(ret_val.stdout.strip())
    if cur_room == 1:
        start_room += " (HIER)"
    elif cur_room == 2:
        garden += " (HIER)"
    elif cur_room == 3:
        end_room += " (HIER)"

    line_len = len(" *                                      *")
    line_len_base = len(" *  A -  *")
    line_len_a = line_len - (line_len_base + len(start_room))
    line_len_b = line_len - (line_len_base + len(garden))
    line_len_c = line_len - (line_len_base + len(end_room))
    print(
        "\n/* **************************************\n" +
        " *                                      *\n" +
        " *  A - " + start_room + line_len_a * " " + " *\n" +
        " *  B - " + garden     + line_len_b * " " + " *\n" +
        " *  C - " + end_room   + line_len_c * " " + " *\n" +
        " *                                      *\n" +
        " *                  ----------------    *\n" +
        " *                  |              |    *\n" +
        " *                  |       C      |    *\n" +
        " *                  |              |    *\n" +
        " *    --------------+------   -----|    *\n" +
        " *    |             |              |    *\n" +
        " *    |      B              A      |    *\n" +
        " *    |             |              |    *\n" +
        " *    --------------+---------------    *\n" +
        " *                                      *\n" +
        " ************************************** */" )

# =================================================================================================================== #
# === IFLOG HELPER COMMANDS === #
# =================================================================================================================== #

def run_iflog_command(command):    
    ret_val = None   
    match command:
        case "help":
            ret_val = subprocess.run(
                ["java", "-jar", IFLOG_JAR, "-h"],
                text=True,capture_output=True)
        case "test":
            ret_val = subprocess.run(
                ["java", "-jar", IFLOG_JAR, IFLOG_FILE, IFLTARGET_FILE, "-s"],
                text=True,capture_output=True)
        case "out":
            ret_val = subprocess.run(
                ["java", "-jar", IFLOG_JAR, IFLOG_FILE, IFLTARGET_FILE, "-o", SQL_OUTPUT_FILE],
                text=True,capture_output=True)
        case "out_log":
            ret_val = subprocess.run(
                ["java", "-jar", IFLOG_JAR, IFLOG_FILE, IFLTARGET_FILE, "-o", SQL_OUTPUT_FILE, "-s", "-l", LOG_FILE],
                text=True,capture_output=True)
        case _:
            print("Befehl " + command + " nicht gefunden!")

    if(ret_val is not None):
        output_command(ret_val.args, ret_val.stdout, ret_val.stderr)

# =================================================================================================================== #
# === CREATE/DROP DATABASE === #
# =================================================================================================================== #

def init():
    run_psql_command("CREATE DATABASE " + DB_NAME + ";")
    run_command("\i " + SQL_OUTPUT_FILE)

def destroy():
    run_psql_command("DROP DATABASE " + DB_NAME + ";")

# =================================================================================================================== #
# === PSQL HELPER COMMANDS === #
# =================================================================================================================== #

def run_psql_command(command):
    ret_val = subprocess.run(
        ["psql", "-U", "postgres", "-c", command],
        text=True,capture_output=True,env=NEW_ENV)
    output_command(ret_val.args, ret_val.stdout, ret_val.stderr)

def run_command(command):
    ret_val = subprocess.run(
        ["psql", "-U", "postgres", "-d", DB_NAME, "-c", command],
        text=True,capture_output=True,env=NEW_ENV)
    output_command(ret_val.args, ret_val.stdout, ret_val.stderr)

def run_game_command(command, options = "-qAt", suppress_newline = False):
    ret_val = subprocess.run(
        ["psql", "-U", "postgres", "-d", DB_NAME, options, "-c", "set client_encoding=win1252", "-c", command],
        text=True,capture_output=True,env=NEW_ENV)
    output_command([command], ret_val.stdout, ret_val.stderr, suppress_newline)

def run_map_command(room):
    ret_room = "???"
    ret_val = subprocess.run(
        ["psql", "-U", "postgres", "-d", DB_NAME, "-qAt", "-c", "set client_encoding=win1252",
         "-c", "SELECT visited FROM room WHERE name = '" + room + "'"],
        text=True,capture_output=True,env=NEW_ENV)
    if ret_val.stdout.strip() != "f":
        ret_val = subprocess.run(
            ["psql", "-U", "postgres", "-d", DB_NAME, "-qAt", "-c", "set client_encoding=win1252",
             "-c", "SELECT sdesc FROM room WHERE name = '" + room + "'"],
             text=True,capture_output=True,env=NEW_ENV)
        ret_room = ret_val.stdout.strip()
    return ret_room

def run_move_command(direction):
    run_game_command(
        "UPDATE character SET loc = path." + direction +
        " FROM path WHERE character.name = 'player' AND character.loc = path.id;", "-qAt")

# =================================================================================================================== #
# === PROCESS INPUT === #
# =================================================================================================================== #

def process_game_input():    
    run_game_command("SELECT gs.msg FROM game_state gs, character c WHERE gs.id = c.game_state;")
    command = input("> ")

    ret = True
    match command:
        case "n" | "north" | "go north":
            run_move_command("north")
        case "s" | "south" | "go south":
            run_move_command("south")
        case "e" | "east" | "go east":
            run_move_command("east")
        case "w" | "west" | "go west":
            run_move_command("west")
        case "i" | "inv" | "inventory":
            run_game_command("SELECT * FROM player_inv;", "-q")
            run_game_command("SELECT * FROM all_inv;", "-q")
        case "m" | "map":
            run_game_command(
                "SELECT r.id, name, sdesc, visited, north, south, east, west FROM room r INNER JOIN path p ON r.id = p.id ;", "-q")
            output_map()
        case "x" | "examine":
            run_game_command("SELECT DoExamine();", "-qAt", True)
        case "a" | "d" | "do" | "do action":
            run_game_command("SELECT DoAction();", "-qAt", True)
        case "q" | "quit" | "exit":
            ret = False
        case _:
            print("Befehl " + command + " nicht gefunden!")
    
    return ret

def process_input():
    command = input("\nBefehl: ")
    ret = True
    
    match command:
        case "h" | "help":
            run_iflog_command("help")
        case "t" | "test":
            run_iflog_command("test")
        case "o" | "out":
            run_iflog_command("out")
        case "l" | "log":
            run_iflog_command("out_log")
        case "i" | "init":
            init()
        case "d" | "destroy":
            destroy()
        case "r" | "reinit":            
            destroy()
            init()
        case "q" | "quit" | "exit":
            ret = False
        case "s" | "start":
            run_game_command(
                "SELECT r.sdesc || E'\n' || r.ldesc FROM character c, room r WHERE c.name = 'player' AND c.loc = r.id")
            cont_game = True
            while cont_game:
                cont_game = process_game_input()
        case _:
            print("Befehl " + command + " nicht gefunden!")
    
    return ret

# =================================================================================================================== #
# === MAIN ENTRY POINT === #
# =================================================================================================================== #

def main():
    # set shell or windows won't display mutated vowels properly
    subprocess.run(["chcp", "1252"],text=True,capture_output=True,shell=True)
    cont = True
    while cont:
        cont = process_input()

if __name__ == "__main__":
    main()

$content = [System.IO.File]::ReadAllText('src/main/java/com/garbagemule/MobArena/ArenaImpl.java')
$content = $content.Replace('else if (running && !offlineArenaPlayers.contains(p.getUniqueId()) && !offlineSpecPlayers.contains(p.getUniqueId()))', 'else if (running && !settings.getBoolean(\"allow-mid-game-join\", false) && !offlineArenaPlayers.contains(p.getUniqueId()) && !offlineSpecPlayers.contains(p.getUniqueId()))')
[System.IO.File]::WriteAllText('src/main/java/com/garbagemule/MobArena/ArenaImpl.java', $content)

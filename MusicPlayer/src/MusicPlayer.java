import java.util.*;
import java.io.*;

class Song {
    private String title;
    private String artist;

    public Song(String title, String artist) {
        this.title = title;
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    @Override
    public String toString() {
        return title + " - " + artist;
    }
}

class Playlist {
    private String name;
    private List<Song> songs;
    private int currentSongIndex;

    public Playlist(String name) {
        this.name = name;
        this.songs = new ArrayList<>();
        this.currentSongIndex = -1; // Нет текущей песни
    }

    public void addSong(Song song) {
        songs.add(song);
    }

    public boolean removeSong(int songIndex) {
        if (songIndex >= 0 && songIndex < songs.size()) {
            songs.remove(songIndex);
            return true;
        }
        return false;
    }

    public Song getCurrentSong() {
        if (currentSongIndex >= 0 && currentSongIndex < songs.size()) {
            return songs.get(currentSongIndex);
        }
        return null;
    }

    // В классе Playlist
    public Song getPreviousSong() {
        if (songs.isEmpty()) {
            return null;
        }
        currentSongIndex = (currentSongIndex - 1 + songs.size()) % songs.size();
        return getCurrentSong();
    }

    public Song getNextSong() {
        if (songs.isEmpty()) {
            return null;
        }
        currentSongIndex = (currentSongIndex + 1) % songs.size();
        return getCurrentSong();
    }

    public void setCurrentSongIndex(int index) {
        if (index >= 0 && index < songs.size()) {
            currentSongIndex = index;
        }
    }

    public void saveToFile(String filename) throws IOException {
        File file = new File(filename);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        for (Song song : songs) {
            writer.write(song.getTitle() + ";" + song.getArtist());
            writer.newLine();
        }
        writer.close();
    }

    public void loadFromFile(String filename) throws IOException {
        File file = new File(filename);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(";");
            if (parts.length == 2) {
                addSong(new Song(parts[0], parts[1]));
            }
        }
        reader.close();
    }
    // Геттер для имени плейлиста
    public String getName() {
        return name;
    }

    // Геттер для списка песен
    public List<Song> getSongs() {
        return new ArrayList<>(songs); // Возвращаем копию списка, чтобы предотвратить изменения извне
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Плейлист: " + name + "\n");
        for (int i = 0; i < songs.size(); i++) {
            sb.append(i + 1).append(". ").append(songs.get(i)).append("\n");
        }
        return sb.toString();
    }

}

public class MusicPlayer {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Map<Integer, Playlist> playlists = new HashMap<>();
    private static int playlistIdCounter = 1;
    private static Playlist currentPlaylist;
    private static Song currentSong;

    public static void main(String[] args) {
        boolean exit = false;
        while (!exit) {
            System.out.println("Музыкальный плеер");
            System.out.println("1. Создать плейлист");
            System.out.println("2. Воспроизвести плейлист");
            System.out.println("3. Добавить песню в плейлист");
            System.out.println("4. Удалить песню из плейлиста");
            System.out.println("5. Показать плейлист");
            System.out.println("6. Сохранить плейлист");
            System.out.println("7. Удалить плейлист");
            System.out.println("8. Воспроизвести предыдущий трек");
            System.out.println("9. Воспроизвести следующий трек");
            System.out.println("10. Повторить текущий трек");
            System.out.println("11. Вывести все песни");
            System.out.println("0. Выход");

            System.out.println("Выберите опцию:");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    createPlaylist();
                    break;
                case 2:
                    playPlaylist();
                    break;
                case 3:
                    loadPlaylistFromFile();
                    break;
                case 4:
                    removeSongFromPlaylist();
                    break;
                case 5:
                    int playlistNumber = scanner.nextInt();
                    showPlaylist(playlistNumber);
                    break;
                case 6:
                    savePlaylist();
                    break;
                case 7:
                    deletePlaylist();
                    break;
                case 8:
                    playPreviousTrack();
                    break;
                case 9:
                    playNextTrack();
                    break;
                case 10:
                    repeatCurrentTrack();
                    break;
                case 11:
                    showAllSongs();
                    break;
                case 0:
                    exit = true;
                    break;
                default:
                    System.out.println("Неверная опция. Пожалуйста, попробуйте снова.");
                    break;
            }
        }

        scanner.close();
        System.out.println("Спасибо за использование музыкального плеера!");
    }

    private static void loadPlaylistFromFile() {
        System.out.println("Введите ID плейлиста для загрузки из файла:");
        int id = scanner.nextInt();
        if (playlists.containsKey(id)) {
            currentPlaylist = playlists.get(id);
            try {
                System.out.println("Введите имя файла для загрузки плейлиста:");
                String filename = scanner.next();
                currentPlaylist.loadFromFile(filename);
                System.out.println("Плейлист загружен из файла '" + filename + "'.");
            } catch (IOException e) {
                System.out.println("Ошибка при загрузке плейлиста: " + e.getMessage());
            }
        } else {
            System.out.println("Плейлист с таким ID не найден.");
        }
    }



    private static void createPlaylist() {
        System.out.println("Введите название плейлиста:");
        String name = scanner.next();
        Playlist playlist = new Playlist(name);
        playlists.put(playlistIdCounter++, playlist);
        System.out.println("Плейлист '" + name + "' создан.");
    }

    private static void playPlaylist() {
        System.out.println("Введите номер плейлиста для воспроизведения:");
        int playlistNumber = scanner.nextInt();
        // Проверяем, есть ли плейлист с таким номером
        if (playlistNumber > 0 && playlistNumber <= playlists.size()) {
            currentPlaylist = playlists.get(playlistNumber);
            if (currentPlaylist != null && !currentPlaylist.getSongs().isEmpty()) {
                currentPlaylist.setCurrentSongIndex(0); // Устанавливаем воспроизведение с первого трека
                currentSong = currentPlaylist.getCurrentSong();
                System.out.println("Воспроизведение плейлиста: " + currentPlaylist.getName());
                System.out.println("Текущий трек: " + currentSong);
            } else {
                System.out.println("Плейлист пуст или не найден.");
            }
        } else {
            System.out.println("Плейлист с таким номером не существует.");
        }
    }

    private static void savePlaylist() {
        if (!playlists.isEmpty()) {
            System.out.println("Введите номер плейлиста для сохранения:");
            int playlistNumber = scanner.nextInt();
            // Проверяем, есть ли плейлист с таким номером
            if (playlistNumber > 0 && playlistNumber <= playlists.size()) {
                Playlist selectedPlaylist = playlists.get(playlistNumber - 1); // Получаем плейлист по номеру
                try {
                    System.out.println("Введите имя файла для сохранения плейлиста:");
                    String filename = scanner.next();
                    selectedPlaylist.saveToFile(filename);
                    System.out.println("Плейлист сохранен в файл '" + filename + "'.");
                } catch (IOException e) {
                    System.out.println("Ошибка при сохранении плейлиста: " + e.getMessage());
                }
            } else {
                System.out.println("Плейлиста с таким номером не существует.");
            }
        } else {
            System.out.println("Нет плейлистов для сохранения.");
        }
    }

    private static void deletePlaylist() {
        System.out.println("Введите ID плейлиста для удаления:");
        int id = scanner.nextInt();
        if (playlists.remove(id) != null) {
            System.out.println("Плейлист удален.");
        } else {
            System.out.println("Плейлист с таким ID не найден.");
        }
    }



    private static void showPlaylist(int playlistNumber) {
        Playlist selectedPlaylist = playlists.get(playlistNumber);
        if (selectedPlaylist != null) {
            System.out.println(selectedPlaylist);
        } else {
            System.out.println("Плейлист с номером " + playlistNumber + " не найден.");
        }
    }

    private static void removeSongFromPlaylist() {
        System.out.println("Введите номер плейлиста:");
        int playlistNumber = scanner.nextInt();
        Playlist playlist = playlists.get(playlistNumber);

        if (playlist != null) {
            System.out.println(playlist);
            System.out.println("Введите номер песни для удаления:");
            int songIndex = scanner.nextInt() - 1;
            if (playlist.removeSong(songIndex)) {
                System.out.println("Песня удалена из плейлиста.");
            } else {
                System.out.println("Песня с таким номером не найдена в плейлисте " + playlistNumber + ".");
            }
        } else {
            System.out.println("Плейлист с номером " + playlistNumber + " не найден.");
        }
    }

    private static void showAllSongs() {
        if (playlists.isEmpty()) {
            System.out.println("Нет плейлистов для отображения песен.");
            return;
        }

        System.out.println("Все песни:");
        for (Playlist playlist : playlists.values()) {
            System.out.println("Плейлист: " + playlist.getName()); // Используем геттер для имени
            for (Song song : playlist.getSongs()) { // Используем геттер для списка песен
                System.out.println(song);
            }
            System.out.println(); // Добавляем пустую строку для разделения плейлистов
        }
    }

    private static void playPreviousTrack() {
        if (currentPlaylist != null) {
            currentSong = currentPlaylist.getPreviousSong();
            if (currentSong != null) {
                System.out.println("Воспроизведение предыдущего трека: " + currentSong);
            }
        } else {
            System.out.println("Нет активного плейлиста.");
        }
    }

    private static void playNextTrack() {
        if (currentPlaylist != null) {
            currentSong = currentPlaylist.getNextSong();
            if (currentSong != null) {
                System.out.println("Воспроизведение следующего трека: " + currentSong);
            }
        } else {
            System.out.println("Нет активного плейлиста.");
        }
    }

    private static void repeatCurrentTrack() {
        if (currentSong != null) {
            System.out.println("Повтор текущего трека: " + currentSong);
        } else {
            System.out.println("Нет трека для повтора.");
        }
    }}
import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

public class FileHasherZipper {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("用法: java FileHasherZipper <文件或文件夹路径>");
            return;
        }

        File input = new File(args[0]);
        if (!input.exists()) {
            System.out.println("路径不存在: " + args[0]);
            return;
        }

        try {
            // 1. 计算 SHA-256 哈希
            String hash;
            if (input.isFile()) {
                hash = getFileHash(input, "SHA-256");
            } else {
                hash = getDirectoryHash(input, "SHA-256");
            }
            System.out.println("哈希 (SHA-256): " + hash);

            // 2. 写入同名 txt
            String txtPath = args[0] + ".txt";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(txtPath))) {
                writer.write(hash);
            }
            System.out.println("哈希已写入: " + txtPath);

            // 3. 调用 7zip 压缩并加密
            String zipPath = args[0] + ".7z";
            List<String> command = new ArrayList<>();
            command.add("7z.exe");
            command.add("a");
            command.add("-p" + hash);
            command.add("-mhe=on");
            command.add(zipPath);
            command.add(args[0]);

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.inheritIO();
            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("压缩完成: " + zipPath);
            } else {
                System.out.println("7z 压缩失败，退出码: " + exitCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 计算文件哈希
    public static String getFileHash(File file, String algorithm) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        try (InputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int n;
            while ((n = fis.read(buffer)) > 0) {
                digest.update(buffer, 0, n);
            }
        }
        return bytesToHex(digest.digest());
    }

    // 计算文件夹哈希（遍历所有文件并累加）
    public static String getDirectoryHash(File dir, String algorithm) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        Files.walk(dir.toPath())
            .filter(Files::isRegularFile)
            .sorted() // 保证顺序一致
            .forEach(path -> {
                try (InputStream fis = new FileInputStream(path.toFile())) {
                    byte[] buffer = new byte[8192];
                    int n;
                    while ((n = fis.read(buffer)) > 0) {
                        digest.update(buffer, 0, n);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        return bytesToHex(digest.digest());
    }

    // 转 hex
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}

package home.work.finance.repository;

import home.work.finance.model.Wallet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WalletRepository extends FileRepository {
    private static final String FILE_PATH = "data/wallets/wallets.json";

    protected void init() {
        File file = new File(FILE_PATH);
        try {
            if (file.getParentFile().mkdirs() && file.createNewFile()) {
                saveWallets(new ArrayList<>());
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при создании файла кошельков: " + e.getMessage());
        }
    }

    public void saveWallets(List<Wallet> wallets) {
        try {
            saveDataToFile(FILE_PATH, wallets);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении кошельков: " + e.getMessage());
        }
    }

    public List<Wallet> loadWallets() {
        try {
            return loadDataFromFile(FILE_PATH, Wallet.class);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при загрузке кошельков: " + e.getMessage());
        }
    }

    public List<Wallet> loadWalletsByUser(String userId) {
        return loadWallets().stream().filter(wallet -> wallet.getUserId().equals(userId)).toList();
    }

    public Wallet loadWalletsByNameAndUser(String walletName, String userId) {
        return loadWallets().stream().filter(wallet -> wallet.getName().equals(walletName) && wallet.getUserId().equals(userId)).findFirst().orElseThrow(() -> new IllegalArgumentException("Кошелёк с названием \"" + walletName + "\" не найден."));
    }

    public void saveWallet(Wallet wallet) {
        List<Wallet> wallets = loadWallets();

        List<Wallet> newWallets = wallets.stream().collect(ArrayList::new, (list, item) -> {
            if (item.getUserId().equals(wallet.getUserId()) && item.getName().equals(wallet.getName())) {
                list.add(wallet);
            } else {
                list.add(item);
            }
        }, ArrayList::addAll);

        if (wallets.stream().noneMatch(item -> item.getName().equals(wallet.getName()) && item.getUserId().equals(wallet.getUserId()))) {
            newWallets.add(wallet);
        }
        saveWallets(newWallets);
    }
}
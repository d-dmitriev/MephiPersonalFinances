package home.work.finance.service;

import home.work.finance.model.Wallet;

import java.util.List;

public interface WalletRepository {
    List<Wallet> loadWalletsByUser(String username);

    void saveWallet(Wallet wallet);

    List<Wallet> loadWallets();

    void saveWallets(List<Wallet> wallets);

    Wallet loadWalletsByNameAndUser(String senderWallet, String username);
}

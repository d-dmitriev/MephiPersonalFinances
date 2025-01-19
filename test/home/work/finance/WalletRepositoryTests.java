package home.work.finance;

import home.work.finance.model.User;
import home.work.finance.model.Wallet;
import home.work.finance.repository.WalletRepositoryImpl;
import home.work.finance.service.WalletRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WalletRepositoryTests {
    private static final WalletRepository walletRepository = new WalletRepositoryImpl();
    @BeforeAll
    static void init() {

    }

    @Test
    void loadWalletsByUser() {
        assertEquals(0, walletRepository.loadWalletsByUser("test1").size());
    }

    @Test
    void loadWalletsByNameAndUser() {
        User user = new User("wallet_load", "");
        String wallet = "wallet_finances";
        walletRepository.saveWallet(new Wallet(user.getUsername(), wallet, 100));
        assertEquals(wallet, walletRepository.loadWalletsByNameAndUser(wallet, user.getUsername()).getName());
        walletRepository.saveWallets(walletRepository.loadWallets().stream().filter(w -> w.getUserId().equals(user.getUsername()) && w.getName().equals(wallet)).toList());
    }
}
